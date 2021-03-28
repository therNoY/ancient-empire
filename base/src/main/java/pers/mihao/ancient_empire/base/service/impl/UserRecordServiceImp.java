package pers.mihao.ancient_empire.base.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.mihao.ancient_empire.auth.util.AuthUtil;
import pers.mihao.ancient_empire.base.bo.*;
import pers.mihao.ancient_empire.base.dto.ArmyConfig;
import pers.mihao.ancient_empire.base.dto.InitMapDTO;
import pers.mihao.ancient_empire.base.dto.ReqSaveRecordDto;
import pers.mihao.ancient_empire.base.entity.RegionMes;
import pers.mihao.ancient_empire.base.entity.UnitMes;
import pers.mihao.ancient_empire.base.entity.UserMap;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.base.enums.ArmyEnum;
import pers.mihao.ancient_empire.base.enums.StateEnum;
import pers.mihao.ancient_empire.base.mongo.dao.UserRecordRepository;
import pers.mihao.ancient_empire.base.service.RegionMesService;
import pers.mihao.ancient_empire.base.service.UnitMesService;
import pers.mihao.ancient_empire.base.service.UserMapService;
import pers.mihao.ancient_empire.base.service.UserRecordService;
import pers.mihao.ancient_empire.base.util.factory.UnitFactory;
import pers.mihao.ancient_empire.common.constant.CatchKey;
import pers.mihao.ancient_empire.common.dto.ApiConditionDTO;
import pers.mihao.ancient_empire.common.jdbc.redis.RedisUtil;
import pers.mihao.ancient_empire.common.util.BeanUtil;
import pers.mihao.ancient_empire.common.util.DateUtil;
import pers.mihao.ancient_empire.common.util.IntegerUtil;
import pers.mihao.ancient_empire.common.util.StringUtil;

@Service
public class UserRecordServiceImp implements UserRecordService {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserRecordRepository userRecordRepository;
    @Autowired
    UserMapService userMapService;
    @Autowired
    UnitMesService unitMesService;
    @Autowired
    RegionMesService regionMesService;
    @Autowired
    MongoTemplate mongoTemplate;

    private static String tempMap = "临时地图";

    /**
     * 根据地图开始游戏 生成存档
     *
     * @param initMapDTO
     * @return
     */
    @Transactional
    @Override
    public UserRecord initMapRecord(InitMapDTO initMapDTO, UserMap userMap) {
        UserRecord userRecord = new UserRecord();
        userRecord.setMaxPop(initMapDTO.getMaxPop());
        // 1.获取地图
        // 1.设置初始化地图
        GameMap map = new GameMap(userMap.getRow(), userMap.getColumn(), userMap.getRegions());
        userRecord.setGameMap(map);
        // 2.设置初始化军队 完善军队信息
        List<ArmyConfig> reqArmies = initMapDTO.getArmyList();
        List<Army> armyList = new ArrayList<>();
        for (int i = 0; i < reqArmies.size(); i++) {
            ArmyConfig armyConfig = reqArmies.get(i);
            if (armyConfig.getType().equals(ArmyEnum.NO.type())) {
                continue;
            }
            Army army = new Army();
            BeanUtils.copyProperties(armyConfig, army);
            army.setId(i);
            List<Unit> units = new ArrayList<>();
            String color = army.getColor();
            AtomicInteger pop = new AtomicInteger();
            userMap.getUnits().stream()
                    .filter(baseUnit -> baseUnit.getColor().equals(color))
                    .forEach(baseUnit -> {
                        Unit unit = UnitFactory.createUnit(baseUnit.getTypeId(), baseUnit.getRow(), baseUnit.getColumn());
                        unit.setTypeId(baseUnit.getTypeId());
                        UnitMes unitMes = unitMesService.getById(unit.getTypeId());
                        pop.set(pop.get() + unitMes.getPopulation());
                        units.add(unit);
                    });
            army.setUnits(units);
            army.setPop(pop.get());
            army.setMoney(initMapDTO.getMoney());
            if (armyConfig.getType().equals(ArmyEnum.USER.type())) {
                army.setPlayer(AuthUtil.getLoginUser().getUsername());
            }
            if (army.getOrder() == 1) {
                userRecord.setCurrPlayer(army.getPlayer());
            }
            armyList.add(army);
        }
        userRecord.setArmyList(armyList);
        String uuid = StringUtil.getUUID();
        userRecord.setUuid(uuid);
        userRecord.setCurrentRound(1);
        userRecord.setCurrPoint(new Site(1, 1));
        userRecord.setCreateUserId(initMapDTO.getUserId());
        Region region = userRecord.getGameMap().getRegions().get(0);
        RegionMes regionMes = regionMesService.getRegionByTypeFromLocalCatch(region.getType());
        RegionInfo regionInfo = BeanUtil.copyValueFromParent(regionMes, RegionInfo.class);
        regionInfo.setColor(region.getColor());
        userRecord.setCurrRegion(regionInfo);
        // TODO 测试
        int random = IntegerUtil.getRandomIn(10);
        int index = 0;
        if (userMap.getMapName().startsWith("测试地图")) {
            List<Tomb> tombList = new ArrayList<>();
            Tomb tomb1 = new Tomb(9, 7, 1);
            tombList.add(tomb1);
            userRecord.setTombList(tombList);
            for (Army army : armyList) {
                for (Unit unit : army.getUnits()) {
                    if (index++ < random) {
                        switch (random) {
                            case 1:
                                unit.setStatus(StateEnum.POISON.type());
                                unit.setStatusPresenceNum(2);
                                break;
                            case 2:
                                unit.setStatus(StateEnum.BLIND.type());
                                unit.setStatusPresenceNum(2);
                                break;
                            case 3:
                                unit.setStatus(StateEnum.WEAK.type());
                                unit.setStatusPresenceNum(2);
                                break;
                            case 4:
                                unit.setStatus(StateEnum.EXCITED.type());
                                unit.setStatusPresenceNum(2);
                                break;
                        }
                        unit.setLevel(IntegerUtil.getRandomIn(3));
                        unit.setLife(IntegerUtil.getRandomIn(100));
                        random = IntegerUtil.getRandomIn(5);
                        index = 0;
                    }
                    if (index > 6) {
                        index = 0;
                    }
                    if (unit.getTypeId().equals(9)) {
                        unit.setExperience(90);
                    }
                }
            }
        }
        // 设置当前军队信息
        for (int i = 0; i < armyList.size(); i++) {
            Army army = armyList.get(i);
            if (army.getOrder() == 1) {
                userRecord.setCurrColor(army.getColor());
                userRecord.setCurrCamp(army.getCamp());
                userRecord.setCurrArmyIndex(i);
                break;
            }
        }

        userRecord.setTemplateId(userMap.getTemplateId());
        // 4.保存记录
        userRecordRepository.save(userRecord);
        return userRecord;
    }

    /**
     * 获取 Record By uuid
     * 这里不使用 Spring catchAble 原因是保存改缓存使用的是redishelper 设置 序列化方式不一样
     *
     * @param uuid
     * @return
     */
    @Override
    public UserRecord getRecordById(String uuid) {
        UserRecord userRecord = null;
        if ((userRecord = RedisUtil.getObject(CatchKey.getKey(CatchKey.USER_RECORD) + uuid, UserRecord.class)) == null) {
            log.info("从mongo获取 {} 的信息", uuid);
            Optional<UserRecord> optional = userRecordRepository.findById(uuid);
            if (optional.isPresent()) {
                userRecord = optional.get();
                RedisUtil.set(CatchKey.getKey(CatchKey.USER_RECORD) + uuid, userRecord, 5 * 60L);
            }
        }
        return userRecord;
    }

    /**
     * 保存记录如果已经存在就返回false
     *
     * @param saveRecordDto
     * @return
     */
    @Override
    public boolean saveRecord(ReqSaveRecordDto saveRecordDto) {

        Integer userId = AuthUtil.getUserId();
        UserRecord record = userRecordRepository.getFirstByCreateUserIdAndRecordName(userId, saveRecordDto.getName());
        if (record != null) {
            return false;
        }
        UserRecord userRecord = getRecordById(saveRecordDto.getUuid());
        userRecord.setRecordName(saveRecordDto.getName());
        userRecord.setCreateTime(DateUtil.getDataTime());
        userRecord.setUnSave(false);
        userRecordRepository.save(userRecord);
        return true;
    }

    /**
     * 保存临时地图
     *
     * @param uuid
     * @return
     */
    @Transactional
    @Override
    public boolean saveTempRecord(String uuid) {
        // 1.删除原来的用户的临时地图
        userRecordRepository.deleteByUnSaveAndCreateUserId(true, AuthUtil.getUserId());
        // 2.添加
        UserRecord userRecord = getRecordById(uuid);
        userRecord.setRecordName(tempMap);
        userRecord.setCreateTime(DateUtil.getDataTime());
        userRecord.setUnSave(true);
        userRecordRepository.save(userRecord);
        return false;
    }

    @Override
    public void removeById(String uuid) {
        userRecordRepository.deleteById(uuid);
    }

    @Override
    public void saveRecord(UserRecord record) {
        userRecordRepository.save(record);
    }

    @Override
    public List<UserRecord> listUserRecordWithPage(ApiConditionDTO apiConditionDTO) {
        Criteria criteria = Criteria.where("createUserId").is(apiConditionDTO.getUserId());
        if (StringUtil.isNotBlack(apiConditionDTO.getCondition())) {
            criteria.and("recordName").is(apiConditionDTO.getCondition());
        }
        Query query = new Query(criteria);
        query.fields().include("recordName");
        query.fields().include("uuid");
        List<UserRecord> records = mongoTemplate.find(query, UserRecord.class);
        return records;
    }
}
