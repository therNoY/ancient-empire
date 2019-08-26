package com.mihao.ancient_empire.service.imp;

import com.mihao.ancient_empire.common.util.RedisHelper;
import com.mihao.ancient_empire.common.util.StringUtil;
import com.mihao.ancient_empire.constant.MqMethodEnum;
import com.mihao.ancient_empire.constant.RedisKey;
import com.mihao.ancient_empire.dto.Army;
import com.mihao.ancient_empire.dto.InitMap;
import com.mihao.ancient_empire.dto.Unit;
import com.mihao.ancient_empire.dto.map_dto.ReqInitMapDto;
import com.mihao.ancient_empire.entity.mongo.UserMap;
import com.mihao.ancient_empire.entity.mongo.UserRecord;
import com.mihao.ancient_empire.mongo.dao.UserRecordRepository;
import com.mihao.ancient_empire.service.UserMapService;
import com.mihao.ancient_empire.service.UserRecordService;
import com.mihao.ancient_empire.util.MqHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserRecordServiceImp implements UserRecordService {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserRecordRepository userRecordRepository;

    @Autowired
    MqHelper mqHelper;

    @Autowired
    UserMapService userMapService;

    @Autowired
    RedisHelper redisHelper;

    private Map<String, UserRecord> recordMap; // key: recordId, value record
    /**
     * 获取初始化地图记录
     * @param reqInitMapDto
     * @return
     */
    @Override
    public String initMapRecord(ReqInitMapDto reqInitMapDto) {
        UserRecord userRecord = new UserRecord();
        userRecord.setMaxPop(reqInitMapDto.getMaxPop());
        // 1.获取地图
        UserMap userMap = userMapService.getEncounterMapById(reqInitMapDto.getMapId());
        if (userMap == null) {
            return null;
        }
        // 1.设置初始化地图
        InitMap map = new InitMap(userMap.getRow(), userMap.getColumn(), userMap.getRegions());
        userRecord.setInitMap(map);
        // 2.设置初始化军队 完善军队信息
        List<Army> armyList = reqInitMapDto.getArmyList();
        for (int i = 0; i < armyList.size(); i++) {
            Army army = armyList.get(i);
            army.setId(i);
            List<Unit> units = new ArrayList<>();
            String color = army.getColor();
            userMap.getUnits().stream()
                    .filter(baseUnit -> baseUnit.getColor().equals(color))
                    .forEach(baseUnit -> {
                        Unit unit = new Unit(baseUnit.getType(), baseUnit.getRow(), baseUnit.getColumn());
                        units.add(unit);
                    });
            army.setUnits(units);
            army.setMoney(reqInitMapDto.getMoney());
        }
        userRecord.setArmyList(armyList);
        String uuid = StringUtil.getUUID();
        userRecord.setUuid(uuid);
        // 4.将record设置到缓存(后续可能放到redis)中 并且通知rabbitMQ 消费这条记录
        redisHelper.set(RedisKey.USER_RECORD_ + uuid, userRecord, 60l);
        mqHelper.sendMongoCdr(MqMethodEnum.ADD_RECORD, userRecord);
        return uuid;
    }

    @Override
    public void initMap() {
        if (recordMap == null) {
            recordMap = new HashMap<>();
        }
    }

    /**
     * 获取 recordId
     * @param uuid
     * @return
     */
    @Override
    public UserRecord getRecordById(String uuid) {
        UserRecord userRecord = null;
        if ((userRecord = redisHelper.getObject(RedisKey.USER_RECORD_ + uuid, UserRecord.class)) == null) {
            log.info("从mongo获取 {} 的信息", uuid);
            Optional<UserRecord> optional = userRecordRepository.findById(uuid);
            if (optional != null && optional.get() != null) {
                userRecord = optional.get();
            }
        }
        return userRecord;
    }

}
