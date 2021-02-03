package pers.mihao.ancient_empire.core.manger;

import java.util.ArrayList;
import java.util.stream.Collectors;
import javafx.util.Pair;
import pers.mihao.ancient_empire.base.bo.*;
import pers.mihao.ancient_empire.base.entity.RegionMes;
import pers.mihao.ancient_empire.base.entity.UnitMes;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.base.service.*;
import pers.mihao.ancient_empire.base.util.AppUtil;
import pers.mihao.ancient_empire.common.util.ApplicationContextHolder;
import pers.mihao.ancient_empire.common.util.BeanUtil;
import pers.mihao.ancient_empire.core.dto.ArmyUnitIndexDTO;
import pers.mihao.ancient_empire.core.eums.StatusMachineEnum;
import pers.mihao.ancient_empire.core.eums.SubStatusMachineEnum;
import pers.mihao.ancient_empire.core.manger.handler.GameHandler;
import pers.mihao.ancient_empire.core.robot.RobotManger;

import java.util.List;

/**
 * 方便处理GameContext类 存放视图方法和基本方法 无业务操作
 *
 * @version 1.0
 * @author mihao
 * @date 2020\10\2 0002 21:25
 */
public abstract class GameContextBaseHandler extends BaseHandler implements GameHandler {


    /**
     * 游戏上下文
     */
    protected GameContext gameContext;


    /**
     * 根据Site 获取regionInfo
     *
     * @param site
     * @return
     */
    protected RegionInfo getRegionInfoBySite(int row, int column) {
        Region region = getRegionBySite(row, column);
        RegionMes regionMes = regionMesService.getRegionByType(region.getType());
        RegionInfo regionInfo = BeanUtil.copyValueFromParent(regionMes, RegionInfo.class);
        regionInfo.setColor(region.getColor());
        return regionInfo;
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
     * 根据颜色判断是否是同一个阵营的
     *
     * @param color
     * @return
     */
    protected boolean colorIsCamp(String color) {
        return currArmy().getCamp() == getCampByColor(color);
    }

    protected int getCampByColor(String color) {
        return getCampByColor(record(), color);
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
     * 根据Site 获取regionInfo
     *
     * @param site
     * @return
     */
    protected RegionInfo getRegionInfoByRegionIndex(Integer reginxIndex) {
        return getRegionInfoBySite(AppUtil.getSiteByMapIndex(reginxIndex, gameMap().getColumn()));
    }

    /**
     * 根据Site 获取regionInfo
     *
     * @param site
     * @return
     */
    protected RegionInfo getRegionInfoBySite(Site site) {
        return getRegionInfoBySite(site.getRow(), site.getColumn());
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
        UnitInfo unitInfo = unitMesService.getUnitInfo(unit.getTypeId(), unit.getLevel());
        BeanUtil.copyValueFromParent(unit, unitInfo);
        unitInfo.setRegionInfo(getRegionInfoBySite(unitInfo.getRow(), unitInfo.getColumn()));
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
     * 获取当前单位 当前位置的 的 攻击范围
     *
     * @param unitMes
     * @param aimP
     * @param userRecord
     * @return
     */
    protected List<Site> getCurrUnitAttachArea() {
        UnitMes unitMes = record().getCurrUnit().getUnitMes();
        Site currentPoint = record().getCurrPoint();
        Integer maxRange = unitMes.getMaxAttachRange();
        List<Site> maxAttach = new ArrayList<>();
        int minI = Math.max(currentPoint.getRow() - maxRange, 1);
        int maxI = Math.min(currentPoint.getRow() + maxRange + 1, gameMap().getRow() + 1);
        int minJ = Math.max(currentPoint.getColumn() - maxRange, 1);
        int maxJ = Math.min(currentPoint.getColumn() + maxRange + 1, gameMap().getRow() + 1);
        for (int i = minI; i < maxI; i++) {
            for (int j = minJ; j < maxJ; j++) {
                if (getSiteLength(i, j, currentPoint.getRow(), currentPoint.getColumn()) <= maxRange && getSiteLength(i, j, currentPoint.getRow(), currentPoint.getColumn()) > 0) {
                    maxAttach.add(new Site(i, j));
                }
            }
        }
        Integer minRange = unitMes.getMinAttachRange();
        List<Site> notAttach = null;
        if (minRange != 1) {
            // 获取无法攻击到的点
            minRange = minRange - 1;
            notAttach = new ArrayList<>();
            minI = Math.max(currentPoint.getRow() - minRange, 0);
            maxI = Math.min(currentPoint.getRow() + minRange + 1, gameMap().getRow());
            minJ = Math.max(currentPoint.getColumn() - minRange, 0);
            maxJ = Math.min(currentPoint.getColumn() + minRange + 1, gameMap().getRow());
            for (int i = minI; i < maxI; i++) {
                for (int j = minJ; j < maxJ; j++) {
                    if (getSiteLength(i, j, currentPoint.getRow(), currentPoint.getColumn()) <= minRange) {
                        notAttach.add(new Site(i, j));
                    }
                }
            }

        }

        int row = gameMap().getRow();
        int column = gameMap().getColumn();
        // 过滤符合条件的点
        List<Site> finalNotAttach = notAttach;
        return maxAttach.stream().filter(site -> {
            // 在地图范围内
            if (site.getRow() <= row && site.getColumn() <= column) {
                // 不在不可攻击范围内
                if (finalNotAttach == null || !finalNotAttach.contains(site)) {
                    return true;
                }
            }
            return false;
        }).collect(Collectors.toList());
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
    public UserRecord record() {
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
    public Army currArmy() {
        return currArmy(record());
    }

    /**
     * game视图
     *
     * @return 当前单位
     */
    public UnitInfo currUnit() {
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
     * site集合是否包含site
     * @param sites
     * @param site
     * @return
     */
    protected boolean containsSite(List<? extends Site> sites, Site site) {
        if (sites != null) {
            for (Site s : sites) {
                if (s.getRow().equals(site) && s.getColumn().equals(site.getColumn())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 获取当前单位的范围 获取所有可触发的区域
     * @param site
     * @return
     */
    protected List<Site> getCurrentUnitCanActionArea(Site site){
        return getUnitCanActionArea(site, currUnit().getUnitMes().getMinAttachRange(), currUnit().getUnitMes().getMaxAttachRange());
    }

    protected List<Site> getUnitCanActionArea(Site site, int minAttachRange, int maxAttachRange){
        List<Site> sites = new ArrayList<>();
        for (int i = site.getRow() - maxAttachRange; i < site.getColumn() + maxAttachRange; i++) {
            for (int j = site.getColumn() - maxAttachRange; j < site.getColumn() + maxAttachRange; j++) {
                int length = getSiteLength(site.getRow(), site.getColumn(), i, j);
                if (length >= minAttachRange && length <= maxAttachRange) {
                    sites.add(new Site(i, j));
                }
            }
        }
        return sites;
    }

    /**
     * 打印上下文信息
     * @return 上下文信息字符串
     */
    public String printlnContext(){
        return gameContext.toString();
    }




}
