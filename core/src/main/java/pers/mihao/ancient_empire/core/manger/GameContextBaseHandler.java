package pers.mihao.ancient_empire.core.manger;

import javafx.util.Pair;
import pers.mihao.ancient_empire.base.bo.*;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.base.service.*;
import pers.mihao.ancient_empire.base.util.AppUtil;
import pers.mihao.ancient_empire.common.util.ApplicationContextHolder;
import pers.mihao.ancient_empire.common.util.BeanUtil;
import pers.mihao.ancient_empire.core.dto.ArmyUnitIndexDTO;
import pers.mihao.ancient_empire.core.eums.StatusMachineEnum;
import pers.mihao.ancient_empire.core.eums.SubStatusMachineEnum;
import pers.mihao.ancient_empire.core.robot.RobotManger;

import java.util.List;

/**
 * 方便处理GameContext类 存放视图方法和基本方法 无业务操作
 *
 * @version 1.0
 * @auther mihao
 * @date 2020\10\2 0002 21:25
 */
public abstract class GameContextBaseHandler implements Handler {

    private static final String CURR_ARMY_IS_NULL = "currArmyIsNull";

    protected GameContext gameContext;

    protected static RegionMesService regionMesService;
    protected static UnitMesService unitMesService;
    protected static UnitLevelMesService unitLevelMesService;
    protected static UnitTransferService unitTransferService;
    protected static UserRecordService userRecordService;
    protected static AbilityService abilityService;


    protected static RobotManger robotManger;

    static {
        regionMesService = ApplicationContextHolder.getBean(RegionMesService.class);
        unitMesService = ApplicationContextHolder.getBean(UnitMesService.class);
        abilityService = ApplicationContextHolder.getBean(AbilityService.class);
        unitLevelMesService = ApplicationContextHolder.getBean(UnitLevelMesService.class);
        unitTransferService = ApplicationContextHolder.getBean(UnitTransferService.class);
        userRecordService = ApplicationContextHolder.getBean(UserRecordService.class);
        robotManger = ApplicationContextHolder.getBean(RobotManger.class);
    }

    /**
     * 根据颜色判断是否是同一个阵营的
     *
     * @param color
     * @return
     */
    protected boolean colorIsCamp(String color) {
        return currArmy().getCamp() == getCampByColor(color);
    }

    protected int getCampByColor(String color) {
        for (Army army : record().getArmyList()) {
            if (color.equals(army.getColor())) {
                return army.getCamp();
            }
        }
        return -1;
    }


    /**
     * 从整个地图根据位置获取单位
     *
     * @param site
     * @return 军队index 和单位
     */
    protected Pair<Integer, Unit> getUnitFromMapBySite(Site site) {
        Army army;
        Unit unit;
        for (int i = 0; i < record().getArmyList().size(); i++) {
            army = record().getArmyList().get(i);
            unit = getUnitFromArmyBySite(army, site);
            if (unit != null) {
                return new Pair<>(i, unit);
            }
        }
        return null;
    }


    /**
     * 从当前军队中获取单位
     *
     * @param site
     * @return
     */
    protected Unit getUnitFromCurrArmyBySite(Site site) {
        return getUnitFromArmyBySite(currArmy(), site);
    }

    /**
     * 基础方法 在指定军队中 指定位置获取单位
     *
     * @param army
     * @param site
     * @return
     */
    protected Unit getUnitFromArmyBySite(Army army, Site site) {
        for (Unit unit : army.getUnits()) {
            if (AppUtil.siteEquals(unit, site)) {
                return unit;
            }
        }
        return null;
    }

    /**
     * 获取单位在地图中的index
     *
     * @param army
     * @param uuid
     * @return
     */
    protected Integer getUnitIndex(Army army, String uuid) {
        if (army != null) {
            for (int i = 0; i < army.getUnits().size(); i++) {
                if (army.getUnits().get(i).getId().equals(uuid)) {
                    return i;
                }
            }
        } else {
            for (Army a : gameContext.getUserRecord().getArmyList()) {
                for (int i = 0; i < a.getUnits().size(); i++) {
                    if (a.getUnits().get(i).getId().equals(uuid)) {
                        return i;
                    }
                }
            }
        }
        return null;
    }


    /**
     * 从当前军队获取当前单位
     *
     * @return
     */
    protected Unit getCurrUnitFromArmy() {
        List<Unit> units = currArmy().getUnits();
        for (int i = 0; i < units.size(); i++) {
            Unit unit = units.get(i);
            if (record().getCurrUnit().getId().equals(unit.getId())) {
                return unit;
            }
        }
        return null;
    }

    /**
     * 根据军队和单位Index 获取单位
     *
     * @param indexDTO
     * @return
     */
    protected Unit getUnitByIndex(ArmyUnitIndexDTO indexDTO) {
        return record().getArmyList().get(indexDTO.getArmyIndex()).getUnits().get(indexDTO.getUnitIndex());
    }

    /**
     * 通过unit 获取单位的info
     *
     * @param indexDTO
     * @return
     */
    protected UnitInfo getUnitInfoByUnit(Unit unit) {
        UnitInfo unitInfo = unitMesService.getUnitInfo(unit.getTypeId().toString(), unit.getLevel());
        BeanUtil.copyValueFromParent(unit, unitInfo);
        return unitInfo;
    }

    /**
     * 通过index 获取单位的info
     *
     * @param indexDTO
     * @return
     */
    protected UnitInfo getUnitInfoByIndex(ArmyUnitIndexDTO indexDTO) {
        return getUnitInfoByUnit(getUnitByIndex(indexDTO));
    }

    /**
     * 从当前单位获取军队
     *
     * @param unitId
     * @return
     */
    protected Unit getUnitFromCurrArmy(String unitId) {
        List<Unit> units = currArmy().getUnits();
        for (int i = 0; i < units.size(); i++) {
            Unit unit = units.get(i);
            if (unitId.equals(unit.getId())) {
                return unit;
            }
        }
        return null;
    }

    /**
     * 获取当前单位的index
     *
     * @return
     */
    protected Integer getCurrUnitIndex() {
        List<Unit> units = currArmy().getUnits();
        for (int i = 0; i < units.size(); i++) {
            Unit unit = units.get(i);
            if (record().getCurrUnit().getId().equals(unit.getId())) {
                return i;
            }
        }
        return null;
    }

    /**
     * 基础方法 根据位置获取地形
     *
     * @param army
     * @param site
     * @return
     */
    protected int getRegionIndexBySite(Site site) {
        return (site.getRow() - 1) * gameMap().getColumn() - 1 + site.getColumn();
    }

    /**
     * 基础方法 根据位置获取地形
     *
     * @param army
     * @param site
     * @return
     */
    protected Region getRegionBySite(Site site) {
        return gameMap().getRegions().get(getRegionIndexBySite(site));
    }

    /**
     * 基础方法 根据位置获取地形
     *
     * @param army
     * @param site
     * @return
     */
    protected Region getRegionBySite(int row, int column) {
        return gameMap().getRegions().get((row - 1) * gameMap().getColumn() - 1 + column);
    }

    protected boolean siteInArea(Site site, List<Site> sites) {
        return sites.contains(site);
    }

    /**
     * 通过地形的index获取地形位置
     *
     * @param index
     * @return
     */
    protected Site getSiteByRegionIndex(Integer index) {
        int gameColumn = gameMap().getColumn();
        int row = (index + 1) / gameColumn + 1;
        int column = (index + 1) % gameColumn;
        return new Site(row, column == 0 ? gameColumn : column);
    }

    /**
     * 获取当前单位的
     *
     * @return
     */
    protected Site getCurrentUnitSite() {
        return new Site(record().getCurrUnit().getRow(), record().getCurrUnit().getColumn());
    }


    /**
     * 判断游戏的状态机是否在这些中
     *
     * @param enums
     * @return
     */
    protected boolean stateIn(StatusMachineEnum... enums) {
        for (StatusMachineEnum statusEnum : enums) {
            if (gameContext.getStatusMachine().equals(statusEnum)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断游戏的状态机是否在这些中
     *
     * @param enums
     * @return
     */
    protected boolean subStateIn(SubStatusMachineEnum... enums) {
        for (SubStatusMachineEnum statusEnum : enums) {
            if (gameContext.getSubStatusMachine().equals(statusEnum)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 判断游戏的状态机是否不在这些中
     *
     * @param enums
     * @return
     */
    protected boolean stateNotIn(StatusMachineEnum... enums) {
        return !stateIn(enums);
    }


    /**
     * 当前记录
     *
     * @return 记录视图
     */
    protected UserRecord record() {
        return gameContext.getUserRecord();
    }

    /**
     * 当前记录
     *
     * @return 记录视图
     */
    protected GameMap gameMap() {
        return record().getGameMap();
    }

    /**
     * game视图
     *
     * @return 当前单位
     */
    protected Army currArmy() {
        return record().getArmyList().get(record().getCurrArmyIndex());
    }


    /**
     * game视图
     *
     * @return 当前单位
     */
    protected UnitInfo currUnit() {
        return record().getCurrUnit();
    }

    /**
     * game视图
     *
     * @return 当前单位
     */
    protected Site currSite() {
        return record().getCurrPoint();
    }


    /**
     * 获取两点的距离
     *
     * @param row
     * @param column
     * @param row2
     * @param column2
     * @return
     */
    protected int getSiteLength(int row, int column, int row2, int column2) {
        return Math.abs(row - row2) + Math.abs(column - column2);
    }

}
