package pers.mihao.ancient_empire.base.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.mihao.ancient_empire.auth.util.AuthUtil;
import pers.mihao.ancient_empire.base.bo.Army;
import pers.mihao.ancient_empire.base.bo.GameMap;
import pers.mihao.ancient_empire.base.bo.Region;
import pers.mihao.ancient_empire.base.bo.RegionInfo;
import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.dto.ReqInitMapDto;
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
import pers.mihao.ancient_empire.base.util.AppUtil;
import pers.mihao.ancient_empire.base.util.factory.UnitFactory;
import pers.mihao.ancient_empire.common.constant.CatchKey;
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
    RedisUtil redisUtil;
    @Autowired
    MongoTemplate mongoTemplate;

    // TODO 这个设计是为了可能出现对缓存和mongo 中的操作过于频繁导致效率不高
    //  搞成内存操作 但是稍微复杂 且可能会在单多机操作出现问题
    private Map<String, UserRecord> recordMap; // key: recordId, value record

    private static String tempMap = "临时地图";

    /**
     * 根据地图开始游戏 生成存档
     *
     * @param reqInitMapDto
     * @return
     */
    @Transactional
    @Override
    public UserRecord initMapRecord(ReqInitMapDto reqInitMapDto, UserMap userMap) {
        UserRecord userRecord = new UserRecord();
        userRecord.setMaxPop(reqInitMapDto.getMaxPop());
        // 1.获取地图
        // 1.设置初始化地图
        GameMap map = new GameMap(userMap.getRow(), userMap.getColumn(), userMap.getRegions());
        userRecord.setGameMap(map);
        // 2.设置初始化军队 完善军队信息
        List<ReqInitMapDto.ReqArmy> reqArmies = reqInitMapDto.getArmyList();
        List<Army> armyList = new ArrayList<>();
        for (int i = 0; i < reqArmies.size(); i++) {
            ReqInitMapDto.ReqArmy reqArmy = reqArmies.get(i);
            if (reqArmy.getType().equals(ArmyEnum.NO.type())) {
                continue;
            }
            Army army = new Army();
            BeanUtils.copyProperties(reqArmy, army);
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
            army.setMoney(reqInitMapDto.getMoney());
            if (reqArmy.getType().equals(ArmyEnum.USER.type())) {
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
        Region region = userRecord.getGameMap().getRegions().get(0);
        RegionMes regionMes = regionMesService.getRegionByType(region.getType());
        RegionInfo regionInfo = BeanUtil.copyValueFromParent(regionMes, RegionInfo.class);
        regionInfo.setColor(region.getColor());
        userRecord.setCurrRegion(regionInfo);
        // TODO 测试
        int random = IntegerUtil.getRandomIn(5);
        int index = 0;
        if (userMap.getMapName().startsWith("测试地图")) {
            List<Site> tomb = new ArrayList<>();
            tomb.add(new Site(9, 7));
            userRecord.setTomb(tomb);
            for (Army army : armyList) {
                for (Unit unit : army.getUnits()) {
                    if (index++ < random) {
                        switch (random) {
                            case 1:
                                unit.setStatus(StateEnum.POISON.type());
                                break;
                            case 2:
                                unit.setStatus(StateEnum.BLIND.type());
                                break;
                            case 3:
                                unit.setStatus(StateEnum.WEAK.type());
                                break;
                            case 4:
                                unit.setStatus(StateEnum.EXCITED.type());
                                break;
                        }
                        unit.setLevel(IntegerUtil.getRandomIn(3));
                        unit.setLife(AppUtil.getArrayByInt(IntegerUtil.getRandomIn(100)));
                        random = IntegerUtil.getRandomIn(5);
                        index = 0;
                    }
                    if (index > 6) {
                        index = 0;
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

    @Override
    public void initMap() {
        if (recordMap == null) {
            recordMap = new HashMap<>();
        }
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
        if ((userRecord = redisUtil.getObject(CatchKey.getKey(CatchKey.USER_RECORD) + uuid, UserRecord.class)) == null) {
            log.info("从mongo获取 {} 的信息", uuid);
            Optional<UserRecord> optional = userRecordRepository.findById(uuid);
            if (optional.isPresent()) {
                userRecord = optional.get();
                redisUtil.set(CatchKey.getKey(CatchKey.USER_RECORD) + uuid, userRecord, 5 * 60L);
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
        userRecord.setCreateTime(DateUtil.getNow());
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
        userRecord.setCreateTime(DateUtil.getNow());
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
}
