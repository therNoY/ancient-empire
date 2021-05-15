package pers.mihao.ancient_empire.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.mihao.ancient_empire.auth.util.LoginUserHolder;
import pers.mihao.ancient_empire.base.bo.Army;
import pers.mihao.ancient_empire.base.bo.GameMap;
import pers.mihao.ancient_empire.base.bo.Region;
import pers.mihao.ancient_empire.base.bo.RegionInfo;
import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.constant.BaseConstant;
import pers.mihao.ancient_empire.base.dao.UserRecordDAO;
import pers.mihao.ancient_empire.base.dto.ArmyConfig;
import pers.mihao.ancient_empire.base.dto.InitMapDTO;
import pers.mihao.ancient_empire.base.dto.ReqSaveRecordDTO;
import pers.mihao.ancient_empire.base.entity.RegionMes;
import pers.mihao.ancient_empire.base.entity.UnitMes;
import pers.mihao.ancient_empire.base.entity.UserMap;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.base.enums.ArmyEnum;
import pers.mihao.ancient_empire.base.service.RegionMesService;
import pers.mihao.ancient_empire.base.service.UnitMesService;
import pers.mihao.ancient_empire.base.service.UserMapService;
import pers.mihao.ancient_empire.base.service.UserRecordService;
import pers.mihao.ancient_empire.base.util.IPageHelper;
import pers.mihao.ancient_empire.base.util.factory.UnitFactory;
import pers.mihao.ancient_empire.common.dto.ApiConditionDTO;
import pers.mihao.ancient_empire.common.util.BeanUtil;
import pers.mihao.ancient_empire.common.util.StringUtil;

@Service
public class UserRecordServiceImp extends ServiceImpl<UserRecordDAO, UserRecord> implements UserRecordService {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserRecordDAO userRecordDAO;
    @Autowired
    UserMapService userMapService;
    @Autowired
    UserRecordService userRecordService;
    @Autowired
    UnitMesService unitMesService;
    @Autowired
    RegionMesService regionMesService;
    private static final String TEMP_MAP = "临时地图";

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
        userRecord.setType(initMapDTO.getGameType());
        userRecord.setMaxPop(initMapDTO.getMaxPop());
        userRecord.setRecordName("系统保存");
        // 1.获取地图
        // 1.设置初始化地图
        GameMap map = new GameMap(userMap.getRow(), userMap
            .getColumn(), userMap.getRegions());
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
                army.setPlayer(initMapDTO.getPlayer().getOrDefault(color, initMapDTO.getUserId().toString()));
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
        userRecord.setUnSave(BaseConstant.YES);
        userRecord.setCreateTime(LocalDateTime.now());
        userRecord.setCreateUserId(initMapDTO.getUserId());
        Region region = userRecord.getGameMap().getRegions().get(0);
        RegionMes regionMes = regionMesService.getRegionByTypeFromLocalCatch(region.getType());
        RegionInfo regionInfo = BeanUtil.copyValueFromParent(regionMes, RegionInfo.class);
        regionInfo.setColor(region.getColor());
        regionInfo.setRow(1);
        regionInfo.setColumn(1);
        regionInfo.setIndex(0);
        userRecord.setCurrRegion(regionInfo);
        // 设置当前军队信息
        for (int i = 0; i < armyList.size(); i++) {
            Army army = armyList.get(i);
            if (army.getOrder() == 1) {
                userRecord.setCurrArmyIndex(i);
                break;
            }
        }

        userRecord.setTemplateId(userMap.getTemplateId());
        userRecord.setMapId(userMap.getUuid());
        // 4.保存记录
        saveOrUpdate(userRecord);
        delOtherUnSaveStandRecord(userRecord.getUuid(), initMapDTO.getUserId());
        return userRecord;
    }

    /**
     * 获取 Record By uuid 这里不使用 Spring catchAble 原因是保存改缓存使用的是redishelper 设置 序列化方式不一样
     *
     * @param uuid
     * @return
     */
    @Override
    public UserRecord getRecordById(String uuid) {
        return userRecordDAO.selectById(uuid);
    }

    /**
     * 保存记录如果已经存在就返回false
     *
     * @param saveRecordDto
     * @return
     */
    @Override
    public boolean saveRecord(ReqSaveRecordDTO saveRecordDto) {

        Integer userId = LoginUserHolder.getUserId();
        UserRecord record = userRecordDAO.getFirstByCreateUserIdAndRecordName(userId, saveRecordDto.getName());
        if (record != null) {
            return false;
        }
        UserRecord userRecord = getRecordById(saveRecordDto.getUuid());
        userRecord.setRecordName(saveRecordDto.getName());
        userRecord.setCreateTime(LocalDateTime.now());
        userRecord.setUnSave(BaseConstant.NO);
        saveOrUpdate(userRecord);
        removeCatch(userRecord.getUuid());
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
        userRecordDAO.delOtherUnSave(uuid, LoginUserHolder.getUserId());
        // 2.添加
        UserRecord userRecord = getRecordById(uuid);
        userRecord.setRecordName(TEMP_MAP);
        userRecord.setCreateTime(LocalDateTime.now());
        userRecord.setUnSave(BaseConstant.YES);
        saveOrUpdate(userRecord);
        removeCatch(userRecord.getUuid());
        return false;
    }

    @Override
    public void removeById(String uuid) {
        userRecordDAO.deleteById(uuid);
        removeCatch(uuid);
    }

    @Override
    public void saveRecord(UserRecord record) {
        saveOrUpdate(record);
        removeCatch(record.getUuid());
    }

    @Override
    public IPage<UserRecord> listUserRecordWithPage(ApiConditionDTO apiConditionDTO) {
        List<UserRecord> records = userRecordDAO.listUserRecordWithPage(apiConditionDTO);
        return IPageHelper.toPage(records, apiConditionDTO);
    }

    @Override
    public void removeCatch(String id) {
        removeCatchByProxy(id);
    }

//    @CacheEvict(CatchKey.USER_RECORD)
    public void removeCatchByProxy(String id) {
    }

    @Override
    public void delOtherUnSaveStandRecord(String uuid, Integer userId) {
        userRecordDAO.delOtherUnSave(uuid, userId);
    }

    @Override
    public void delById(String uuid, Integer userId) {
        QueryWrapper<UserRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("uuid", uuid)
            .eq("create_user_id", userId);
        userRecordDAO.delete(wrapper);
    }
}
