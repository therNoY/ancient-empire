package pers.mihao.ancient_empire.core.manger.handler;

import com.alibaba.fastjson.JSONObject;
import javafx.util.Pair;
import org.springframework.context.annotation.Bean;
import org.springframework.util.Assert;
import pers.mihao.ancient_empire.base.bo.*;
import pers.mihao.ancient_empire.base.entity.UnitMes;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.base.service.RegionMesService;
import pers.mihao.ancient_empire.base.service.UnitLevelMesService;
import pers.mihao.ancient_empire.base.service.UnitMesService;
import pers.mihao.ancient_empire.base.util.AppUtil;
import pers.mihao.ancient_empire.common.util.ApplicationContextHolder;
import pers.mihao.ancient_empire.common.util.BeanUtil;
import pers.mihao.ancient_empire.common.util.ReflectUtil;
import pers.mihao.ancient_empire.core.constans.ExtMes;
import pers.mihao.ancient_empire.core.dto.ArmyUnitIndexDTO;
import pers.mihao.ancient_empire.core.dto.UnitStatusInfoDTO;
import pers.mihao.ancient_empire.core.eums.StatusMachineEnum;
import pers.mihao.ancient_empire.core.manger.command.Command;
import pers.mihao.ancient_empire.core.manger.command.GameCommand;
import pers.mihao.ancient_empire.core.manger.event.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 基础处理类 存放视图方法和基本方法 无业务操作
 *
 * @version 1.0
 * @auther mihao
 * @date 2020\10\2 0002 21:25
 */
public abstract class BaseHandler extends AbstractGameEventHandler {

    private static final String CURR_ARMY_IS_NULL = "currArmyIsNull";

    private Army currArmy;

    protected static RegionMesService regionMesService;
    protected static UnitMesService unitMesService;
    protected static UnitLevelMesService unitLevelMesService;

    static {
        regionMesService = ApplicationContextHolder.getBean(RegionMesService.class);
        unitMesService = ApplicationContextHolder.getBean(UnitMesService.class);
        unitLevelMesService = ApplicationContextHolder.getBean(UnitLevelMesService.class);
    }

    @Override
    public final List<Command> handler(Event event) {
        List<Command> commandList = super.handler(event);
        // 根据时间处理
        for (Command command : commandList) {
            GameCommand gameCommand = (GameCommand) command;
            JSONObject extMes = gameCommand.getExtMes();
            switch (gameCommand.getGameCommendEnum()) {
                case CHANGE_UNIT_STATUS:
                    UnitStatusInfoDTO unitStatus = (UnitStatusInfoDTO) extMes.get(ExtMes.UNIT_STATUS);
                    updateUnitInfo(getUnitByIndex(unitStatus), unitStatus);
                    break;
            }
        }
        return commandList;
    }

    private void updateUnitInfo(Unit unit, Object from) {
        BeanUtil.copyValue(from, unit);
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
     * 基础方法
     *
     * @param site
     * @return
     */
    protected Unit getUnitFromCurrArmyBySite(Site site) {
        return getUnitFromArmyBySite(currArmy(), site);
    }

    /**
     * 基础方法
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

    protected Unit getUnitByIndex(ArmyUnitIndexDTO indexDTO) {
        return record().getArmyList().get(indexDTO.getArmyIndex()).getUnits().get(indexDTO.getUnitIndex());
    }

    protected Unit getUnitFromArmy(String unitId) {
        List<Unit> units = currArmy().getUnits();
        for (int i = 0; i < units.size(); i++) {
            Unit unit = units.get(i);
            if (unitId.equals(unit.getId())) {
                return unit;
            }
        }
        return null;
    }

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
    protected Region getRegionBySite(Site site) {
        return gameMap().getRegions().get((site.getRow() - 1) * gameMap().getColumn() - 1 + site.getColumn());
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
     * 获取当前单位 当前位置的 的 攻击范围
     *
     * @param unitMes
     * @param aimP
     * @param userRecord
     * @return
     */
    protected List<Site> getAttachArea() {
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
        if (currArmy == null) {
            for (Army army : record().getArmyList()) {
                if (army.getCamp().equals(record().getCurrCamp())) {
                    this.currArmy = army;
                    break;
                }
            }
        }
        Assert.notNull(currArmy, CURR_ARMY_IS_NULL);
        return currArmy;
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
    private int getSiteLength(int row, int column, int row2, int column2) {
        return Math.abs(row - row2) + Math.abs(column - column2);
    }

}
