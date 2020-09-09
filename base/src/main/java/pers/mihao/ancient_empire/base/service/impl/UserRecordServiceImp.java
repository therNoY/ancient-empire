package pers.mihao.ancient_empire.base.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.mihao.ancient_empire.auth.util.AuthUtil;
import pers.mihao.ancient_empire.base.bo.Army;
import pers.mihao.ancient_empire.base.bo.GameMap;
import pers.mihao.ancient_empire.base.bo.Position;
import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.mongo.dao.UserRecordRepository;
import pers.mihao.ancient_empire.base.dto.ReqInitMapDto;
import pers.mihao.ancient_empire.base.dto.ReqSaveRecordDto;
import pers.mihao.ancient_empire.base.entity.UnitMes;
import pers.mihao.ancient_empire.base.entity.UserMap;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.base.enums.ColorEnum;
import pers.mihao.ancient_empire.base.enums.StateEnum;
import pers.mihao.ancient_empire.base.service.UnitMesService;
import pers.mihao.ancient_empire.base.service.UserMapService;
import pers.mihao.ancient_empire.base.service.UserRecordService;
import pers.mihao.ancient_empire.common.constant.MqMethodEnum;
import pers.mihao.ancient_empire.common.constant.RedisKey;
import pers.mihao.ancient_empire.common.util.DateUtil;
import pers.mihao.ancient_empire.common.util.MqHelper;
import pers.mihao.ancient_empire.common.util.RedisHelper;
import pers.mihao.ancient_empire.common.util.StringUtil;

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
    UnitMesService unitMesService;
    @Autowired
    RedisHelper redisHelper;
    @Autowired
    MongoTemplate mongoTemplate;

    // TODO 这个设计是为了可能出现对缓存和mongo 中的操作过于频繁导致效率不高
    //  搞成内存操作 但是稍微复杂 且可能会在单多机操作出现问题
    private Map<String, UserRecord> recordMap; // key: recordId, value record

    private static String tempMap = "临时地图";
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
        GameMap map = new GameMap(userMap.getRow(), userMap.getColumn(), userMap.getRegions());
        userRecord.setGameMap(map);
        // 2.设置初始化军队 完善军队信息
        List<Army> armyList = reqInitMapDto.getArmyList();
        for (int i = 0; i < armyList.size(); i++) {
            Army army = armyList.get(i);
            army.setId(i);
            List<Unit> units = new ArrayList<>();
            String color = army.getColor();
            AtomicInteger pop = new AtomicInteger();
            userMap.getUnits().stream()
                    .filter(baseUnit -> baseUnit.getColor().equals(color))
                    .forEach(baseUnit -> {
                        Unit unit = new Unit(baseUnit.getType(), baseUnit.getRow(), baseUnit.getColumn());
                        UnitMes unitMes = unitMesService.getByType(unit.getType());
                        pop.set(pop.get() + unitMes.getPopulation());
                        units.add(unit);
                    });
            army.setUnits(units);
            army.setPop(pop.get());
            army.setMoney(reqInitMapDto.getMoney());
        }
        userRecord.setArmyList(armyList);
        String uuid = StringUtil.getUUID();
        userRecord.setUuid(uuid);
        userRecord.setCurrentRound(1);
        // TODO 测试
        if (userMap.getMapName().startsWith("测试地图")) {
            userRecord.setTomb(Arrays.asList(new Position(9, 7)));
            for (Army army : armyList) {
                if (army.getColor().equals(ColorEnum.RED.type())) {
                    for (Unit unit : army.getUnits()) {
                        if (unit.getRow() == 3 && unit.getColumn() == 9){
                            unit.setStatus(StateEnum.EXCITED.type());
                        }
                    }
                }
            }
        }
        for (Army army : armyList) {
            if (army.getOrder() == 1) {
                userRecord.setCurrColor(army.getColor());
                userRecord.setCurrCamp(army.getCamp());
                break;
            }
        }
        // 4.将record设置到缓存(后续可能放到redis)中 并且通知rabbitMQ 消费这条记录
        redisHelper.set(RedisKey.USER_RECORD_ + uuid, userRecord, 5 * 60l);
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
     * 获取 Record By uuid
     * 这里不使用 Spring catchAble 原因是保存改缓存使用的是redishelper 设置 序列化方式不一样
     * @param uuid
     * @return
     */
    @Override
    public UserRecord getRecordById(String uuid) {
        UserRecord userRecord = null;
        if ((userRecord = redisHelper.getObject(RedisKey.USER_RECORD_ + uuid, UserRecord.class)) == null) {
            log.info("从mongo获取 {} 的信息", uuid);
            Optional<UserRecord> optional = userRecordRepository.findById(uuid);
            if (optional.isPresent()) {
                userRecord = optional.get();
                redisHelper.set(RedisKey.USER_RECORD_ + uuid, userRecord, 5 * 60l);
            }
        }
        return userRecord;
    }

    /**
     * 保存记录如果已经存在就返回false
     * @param saveRecordDto
     * @return
     */
    @Override
    public boolean saveRecord(ReqSaveRecordDto saveRecordDto) {

        Integer userId = AuthUtil.getAuthId();
        UserRecord record = userRecordRepository.getFirstByCreateUserIdAndRecordName(userId, saveRecordDto.getName());
        if (record != null) {
            return false;
        }
        UserRecord userRecord = getRecordById(saveRecordDto.getUuid());
        userRecord.setRecordName(saveRecordDto.getName());
        userRecord.setCreateTime(DateUtil.getNow());
        userRecord.setUnSave(false);
        userRecordRepository.save(userRecord);
        return true;
    }

    /**
     * 保存临时地图
     * @param uuid
     * @return
     */
    @Transactional
    @Override
    public boolean saveTempRecord(String uuid) {
        // 1.删除原来的用户的临时地图
        userRecordRepository.deleteByUnSaveAndCreateUserId(true, AuthUtil.getAuthId());
        // 2.添加
        UserRecord userRecord = getRecordById(uuid);
        userRecord.setRecordName(tempMap);
        userRecord.setCreateTime(DateUtil.getNow());
        userRecord.setUnSave(true);
        userRecordRepository.save(userRecord);
        return false;
    }
}
