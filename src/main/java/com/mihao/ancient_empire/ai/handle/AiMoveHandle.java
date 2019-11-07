package com.mihao.ancient_empire.ai.handle;

import com.mihao.ancient_empire.ai.RobotManger;
import com.mihao.ancient_empire.ai.constant.AiActiveEnum;
import com.mihao.ancient_empire.ai.dto.UnitActionResult;
import com.mihao.ancient_empire.ai.dto.ActiveResult;
import com.mihao.ancient_empire.ai.dto.SelectUnitResult;
import com.mihao.ancient_empire.constant.*;
import com.mihao.ancient_empire.dto.*;
import com.mihao.ancient_empire.dto.ws_dto.ReqUnitIndexDto;
import com.mihao.ancient_empire.dto.ws_dto.RespEndResultDto;
import com.mihao.ancient_empire.entity.Ability;
import com.mihao.ancient_empire.entity.RegionMes;
import com.mihao.ancient_empire.entity.UnitLevelMes;
import com.mihao.ancient_empire.entity.UnitMes;
import com.mihao.ancient_empire.entity.mongo.UserRecord;
import com.mihao.ancient_empire.handle.end.EndHandle;
import com.mihao.ancient_empire.service.RegionMesService;
import com.mihao.ancient_empire.service.UnitLevelMesService;
import com.mihao.ancient_empire.util.AppUtil;
import com.mihao.ancient_empire.util.ApplicationContextHolder;
import com.mihao.ancient_empire.websocket.service.WsActionService;
import com.mihao.ancient_empire.websocket.service.WsMoveAreaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * record 单例 改类维持在robot manger 中
 */
public class AiMoveHandle extends AiActiveHandle {

    Logger log = LoggerFactory.getLogger(this.getClass());

    private static WsMoveAreaService moveAreaService;
    private static WsActionService actionService;
    private static RegionMesService regionMesService;
    private static UnitLevelMesService unitLevelMesService;

    public AiMoveHandle(String record) {
        log.warn("创建一次 AiMoveHandle {}", record);
    }

    /*保存和类有关的数据*/
    UserRecord record;
    Army army;
    Unit selectUnit;
    Site currSite;
    List<Ability> abilityList;
    List<Position> moveArea = null;
    UnitMes unitMes;
    RobotManger robotManger;
    List<String> campColors;

    static {
        moveAreaService = ApplicationContextHolder.getBean(WsMoveAreaService.class);
        actionService = ApplicationContextHolder.getBean(WsActionService.class);
        unitLevelMesService = ApplicationContextHolder.getBean(UnitLevelMesService.class);
        regionMesService = ApplicationContextHolder.getBean(RegionMesService.class);
    }

    /**
     * 获取一个行动的结果
     *
     * @param record
     * @return
     */
    @Override
    public ActiveResult getActiveResult(UserRecord record) {
        // 1. 获取上一次选择的结果 重新设置选择单位的信息
        this.record = record;
        this.robotManger = RobotManger.getInstance(record);
        SelectUnitResult selectUnitResult = RobotManger.getSelectResult(record.getUuid());// 上一次行动的结果
        this.army = record.getArmyList().get(selectUnitResult.getArmyIndex());
        this.selectUnit = army.getUnits().get(selectUnitResult.getUnitIndex());
        this.currSite = AppUtil.getPosition(selectUnit);
        this.abilityList = abilityService.getUnitAbilityListByType(selectUnit.getType());
        this.unitMes = unitMesService.getByType(selectUnit.getType());
        this.campColors = AppUtil.getCampColors(record);
        ReqUnitIndexDto reqUnitIndexDto = new ReqUnitIndexDto(selectUnitResult.getArmyIndex(), selectUnitResult.getUnitIndex());
        Object object = moveAreaService.getMoveArea(record, reqUnitIndexDto, selectUnit, false);
        if (object instanceof List) {
            this.moveArea = (List<Position>) object;
        } else {
            log.error("获取移动区域错误");
        }
        log.info("准备进行行动的单位: {}", selectUnit.getType());

        if (selectUnit.isDone() || selectUnit.isDead()) {
            log.error("单位无法移动");
        }

        // 2. 获取单位所有的直观的可以进行的行动
        List<UnitActionResult> actionList = new ArrayList();
        Set<Site> attachArea = new HashSet<>();
        // 2.1 获取单位是否可以占领或者修复
        moveArea.stream().forEach(site -> {
            BaseSquare square = AppUtil.getRegionByPosition(record, site);
            List<Position> area = actionService.getAttachArea(unitMes, site, record);
            if (canRepair(square)) {
                log.info("{} 可以进行 修复 操作地点:{}", selectUnit.getType(), site);
                actionList.add(new UnitActionResult(record.getUuid(), AiActiveEnum.REPAIR, site));
            }
            if (canOccupyVillage(square) || canOccupyCastle(square)) {
                log.info("{} 可以进行 占领 操作地点:{}", selectUnit.getType(), site);
                actionList.add(new UnitActionResult(record.getUuid(), AiActiveEnum.OCCUPIED, site));
            }
            attachArea.addAll(area);
        });

        // 2.2 获取单位其他能力
        boolean hasSummoner = abilityList.contains(AbilityEnum.SUMMONER.ability());
        boolean hasHealer = abilityList.contains(AbilityEnum.HEALER.ability());

        attachArea.stream().forEach(site -> {
            // 拥有召唤能力
            if (hasSummoner) {
                if (record.getTomb() != null && record.getTomb().contains(site)) {
                    log.info("{} 可以进行 召唤 操作地点:{}", selectUnit.getType(), site);
                    actionList.add(new UnitActionResult(record.getUuid(), AiActiveEnum.SUMMON, site));
                }
            }

            // 拥有治疗能力
            if (hasHealer) {
                Unit unit = AppUtil.getUnitByPosition(record, site, army.getCamp());
                if (unit != null && !unit.getStatus().equals(StateEnum.POISON.type())) {
                    if (AppUtil.getUnitLeft(unit) < 100) {
                        log.info("{} 可以进行 治疗 操作目标单位:{} 血量：{}", selectUnit.getType(), unit.getType(), AppUtil.getUnitLeft(unit));
                        actionList.add(new UnitActionResult(record.getUuid(), AiActiveEnum.HEAL, site, unit));
                    }
                }
            }

            // 选择攻击情况
            Unit unit = AppUtil.getUnitByPositionNotIn(record, site, army.getCamp());
            if (unit != null) {
                log.info("{} 可以进行 攻击 操作目标单位:{}", selectUnit.getType(), unit.getType());
                actionList.add(new UnitActionResult(record.getUuid(), AiActiveEnum.ATTACH, site, unit));
            }
        });

        // 2.3 给每个操作进行打分 选出一个最好的操作
        if (actionList.size() > 0) {
            log.info("有许多可选的操作size = {} 选出一个最好的操作", actionList.size());
            UnitActionResult preferredAction;
            if ((preferredAction = getPreferredAction(actionList)) != null) {
                return preferredAction;
            } else {
                Site site = getPreferredStandbyPosition();
                return new UnitActionResult(record.getUuid(), AiActiveEnum.END, site);
            }
        }


        log.info("没有直接可以进行的操作 准备获取预计操作");
        // 3. 单位没有可以直接进行的操作 获取将来式操作
        if (robotManger.isThreatened(currSite)) {
            log.info("自己的领地受到威胁 直接结束");
            return new UnitActionResult(record.getUuid(), AiActiveEnum.END, currSite, selectUnit);
        }
        if (abilityList.contains(AbilityEnum.VILLAGE_GET.ability())) {
            Site targetSite = getCanBeOccVillage();
            if (targetSite != null) {
                log.info("有占领村庄的能力准备选择最近的可占领的村庄{}", targetSite);
                Site site = getNextPositionToTarget(targetSite);
                robotManger.addDangerVillage(targetSite);
                return new UnitActionResult(record.getUuid(), AiActiveEnum.MOVE_UNIT, site, selectUnit, moveArea);
            }
        }
        Unit enemyLoad;

        if ((enemyLoad = getNearestEnemyCommander()) != null) {
            log.info("找到最近的敌军指挥官 准备并向他移动");
            Site site = getNextPositionToTarget(AppUtil.getPosition(enemyLoad));
            return new UnitActionResult(record.getUuid(), AiActiveEnum.MOVE_UNIT, site, selectUnit, moveArea);
        } else {
            log.info("没有找到最近的敌军指挥官");
            if (abilityList.contains(AbilityEnum.CASTLE_GET.ability())) {
                Site castleSite = getCanBeOccCastle();
                if (castleSite != null) {
                    log.info("司令官找到最近的可占领的城堡{}", castleSite);
                    Site site = getNextPositionToTarget(castleSite);
                    return new UnitActionResult(record.getUuid(), AiActiveEnum.MOVE_UNIT, site, selectUnit, moveArea);
                } else {
                    Site niceSite = getPreferredStandbyPosition();
                    log.info("没找到最近可占领的城堡，找到最远移动的点{}", niceSite);
                    return new UnitActionResult(record.getUuid(), AiActiveEnum.MOVE_UNIT, niceSite, selectUnit, moveArea);
                }
            } else {
                Unit nearestEnemy;
                if ((nearestEnemy = getNearestEnemy()) != null) {
                    Site nextPosition = getNextPositionToTarget(AppUtil.getPosition(nearestEnemy));
                    log.info("普通单位找到最近的可攻击的单位{}", nextPosition);
                    return new UnitActionResult(record.getUuid(), AiActiveEnum.MOVE_UNIT, nextPosition, selectUnit, moveArea);
                } else {
                    Site niceSite = getPreferredStandbyPosition();
                    log.info("没有可攻击的单位找到最好的移动地点{}", niceSite);
                    return new UnitActionResult(record.getUuid(), AiActiveEnum.MOVE_UNIT, niceSite, selectUnit, moveArea);

                }
            }
        }
    }

    /**
     * 根据行动选择最佳行动
     *
     * @param actionList
     * @return
     */
    private UnitActionResult getPreferredAction(List<UnitActionResult> actionList) {

        UnitActionResult niceAction = actionList.get(0);
        int maxActionScore = Integer.MIN_VALUE;
        for (UnitActionResult unitActionResult : actionList) {
            int score = getActionScore(unitActionResult);
            if (score > maxActionScore) {
                niceAction = unitActionResult;
                maxActionScore = score;
            }
        }
        return niceAction;
    }

    private int getActionScore(UnitActionResult action) {
        int score = 0;
        switch (action.getResultEnum()) {
            case OCCUPIED:
                BaseSquare tile = AppUtil.getRegionByPosition(record, action.getSite());
                if (tile != null && tile.getType().equals(RegionEnum.CASTLE.type())) {
                    score += 20000;
                }
                if (tile != null && tile.getType().equals(RegionEnum.TOWN.type())) {
                    score += 10000;
                }
                break;
            case REPAIR:
                score += 5000;
                break;
            case SUMMON:
                score += 4000;
                break;
            case HEAL:
                Unit target = action.getUnit();
                UnitLevelMes targetUnitLevelMes = unitLevelMesService.getUnitLevelMes(target.getType(), target.getLevel());
                score += 10 * (targetUnitLevelMes.getMaxAttack() * AppUtil.getUnitLeft(target) / 100 + targetUnitLevelMes.getSpeed() * 5);
                break;
            case ATTACH:
                Unit targetUnit = action.getUnit();
                UnitMes targetUnitMes = unitMesService.getByType(targetUnit.getType());
                score += targetUnitMes.getPrice() / 20 + getAttackScore(targetUnit, targetUnitMes);
                break;
            default:
                score += 0;
        }
        if (!abilityList.contains(AbilityEnum.ASSAULT.ability())) {
            score += getStandbyScore(action.getSite());
        }

        if (robotManger.isThreatened(currSite) && !action.getSite().equals(currSite)) {
            BaseSquare baseSquare = AppUtil.getRegionByPosition(record, currSite);
            if (baseSquare.getType().contains(RegionEnum.CASTLE.type())) {
                score -= 20000;
            }
            if (baseSquare.getType().contains(RegionEnum.TOWN.type())) {
                score -= 10000;
            }
        }
        log.info("行动 {} 评分{}", action.getResultEnum(), score);
        return score;
    }

    /**
     * 找到最近的敌军
     *
     * @return
     */
    private Unit getNearestEnemy() {
        Unit enemyEnemy = null;
        int minDistance = Integer.MAX_VALUE;
        for (Army army : record.getArmyList()) {
            if (army.getCamp() != this.army.getCamp()) {
                for (Unit unit : army.getUnits()) {
                    int distance = AppUtil.getLength(unit, selectUnit);
                    if (distance < minDistance) {
                        enemyEnemy = unit;
                        minDistance = distance;
                    }
                }
            }
        }
        return enemyEnemy;
    }

    private int getAttackScore(Unit defender, UnitMes targetUnitMes) {
        int score = 0;
        int lastLeft = AppUtil.getUnitLeft(defender);
        int left = AppUtil.getUnitLeft(selectUnit);
        if (defender.getType().equals(UnitEnum.LORD.type())) {
            score += (left - lastLeft) / 10 * targetUnitMes.getPrice() * defender.getLevel();
        } else {
            score += (left - lastLeft) / 20 * targetUnitMes.getPrice() * defender.getLevel();
        }
        if (defender.getStatus() == null) {
            if (abilityList.contains(AbilityEnum.POISONING.ability()) && abilityList.contains(AbilityEnum.BLINDER.ability())) {
                score += targetUnitMes.getPrice() / 4;
            }
        } else {
            if (defender.getStatus().equals(StateEnum.EXCITED.type())) {
                if (abilityList.contains(AbilityEnum.POISONING.ability()) && abilityList.contains(AbilityEnum.BLINDER.ability())) {
                    score += targetUnitMes.getPrice() / 2;
                }
            }

        }
        return score;
    }

    /**
     * 找到最近的敌军指挥官
     *
     * @return
     */
    private Unit getNearestEnemyCommander() {
        Unit enemyCommander = null;
        int minDistance = Integer.MAX_VALUE;
        for (Army army : record.getArmyList()) {

            if (army.getCamp() != this.army.getCamp()) {
                for (Unit unit : army.getUnits()) {

                    if (unit.getType().equals(UnitEnum.LORD.type())) {
                        int distance = AppUtil.getLength(unit, selectUnit);
                        if (distance < minDistance) {
                            enemyCommander = unit;
                            minDistance = distance;
                        }
                    }
                }
            }
        }
        return enemyCommander;
    }

    /**
     * 获取离目标最近的点
     *
     * @param targetSite
     * @return
     */
    private Site getNextPositionToTarget(Site targetSite) {
        Site next = moveArea.get(0);
        int minDistance = Integer.MAX_VALUE;
        for (Site site : moveArea) {
            if (!AppUtil.isUnitsContentSite(army.getUnits(), site)) {
                int distance = AppUtil.getLength(site, targetSite);
                if (distance < minDistance) {
                    next = site;
                    minDistance = distance;
                }
            }
        }

        return next;
    }


    /**
     * 获取一个最近的可以被占领的点
     *
     * @return
     */
    private Site getCanBeOccCastle() {
        Site targetSite = null;
        int minDistance = Integer.MAX_VALUE;
        for (SiteSquare siteSquare : robotManger.getCastleSite()) {
            if (!campColors.contains(siteSquare.getSquare().getColor())) {
                if (!robotManger.getDangerCastle().contains(siteSquare.getSite())) {
                    int distance = AppUtil.getLength(siteSquare.getSite(), AppUtil.getPosition(selectUnit));
                    if (distance < minDistance) {
                        targetSite = siteSquare.getSite();
                        minDistance = distance;
                    }
                }
            }

        }
        return targetSite;
    }

    /**
     * 获取一个最近的可以被占领的点
     *
     * @return
     */
    private Site getCanBeOccVillage() {
        Site targetSite = null;
        int minDistance = Integer.MAX_VALUE;
        for (SiteSquare siteSquare : robotManger.getCastleSite()) {

            if (!campColors.contains(siteSquare.getSquare().getColor())) {
                if (!robotManger.getDangerVillage().contains(siteSquare.getSite())) {
                    int distance = AppUtil.getLength(siteSquare.getSite(), AppUtil.getPosition(selectUnit));
                    if (distance < minDistance) {
                        targetSite = siteSquare.getSite();
                        minDistance = distance;
                    }
                }
            }
        }
        return targetSite;
    }

    /**
     * 判断单位是否可以修复
     *
     * @param square
     * @return
     */
    private boolean canRepair(BaseSquare square) {
        if (square.getType().equals(RegionEnum.RUINS.type())) {
            if (abilityList.contains(AbilityEnum.REPAIR.ability())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否可以占领
     *
     * @param square
     * @return
     */
    private boolean canOccupyVillage(BaseSquare square) {
        if (square.getType().equals(RegionEnum.TOWN.type()) && !campColors.contains(square.getColor())) {
            if (abilityList.contains(AbilityEnum.VILLAGE_GET.ability())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否可以占领
     *
     * @param square
     * @return
     */
    private boolean canOccupyCastle(BaseSquare square) {
        if (abilityList.contains(AbilityEnum.CASTLE_GET.ability())) {
            if (square.getType().equals(RegionEnum.CASTLE.type()) && !campColors.contains(square.getColor())) {
                return true;
            }
        }
        return false;
    }


    /**
     * 获取移动区域内待着最好的一点
     *
     * @return
     */
    private Site getPreferredStandbyPosition() {
        Site niceSite = moveArea.get(0);
        int max_standby_score = Integer.MIN_VALUE;
        for (Site site : moveArea) {
            if (!AppUtil.isUnitsContentSite(army.getUnits(), site)) {
                int score = getStandbyScore(site);
                if (score > max_standby_score) {
                    niceSite = site;
                    max_standby_score = score;
                }
            }
        }
        return niceSite;
    }

    /**
     * 获取待在目标点的分数
     *
     * @param site 目标点
     * @return
     */
    private int getStandbyScore(Site site) {
        int score = 0;
        score += getAverageEnemyDistance(site) * 20; // 敌军近加分
        score -= getAverageAllyDistance(site) * 10; // 友军近减分
        BaseSquare square = AppUtil.getRegionByPosition(record, site);
        RegionMes regionMes = regionMesService.getRegionByType(square.getType());
        score += regionMes.getBuff() * 5;// 地形防御加分
        score += getTerrainHeal(regionMes, square) * 10; // 恢复血量加分

        if (record.getTomb() != null && record.getTomb().contains(site)) {
            if (!abilityList.contains(AbilityEnum.UNDEAD.ability())) {
                score -= unitMes.getPrice() / 4;
            }
            if (abilityList.contains(AbilityEnum.UNDEAD.ability())) {
                score += 200;
            }
        }
        if (!campColors.contains(square.getColor())) {
            score -= 50 * unitMes.getPrice() / 20;
        }

        if (robotManger.isThreatened(site)) {
            if (square.getType().equals(RegionEnum.CASTLE.type())) {
                score += 20000;
            }
            if (square.getType().equals(RegionEnum.TOWN.type())) {
                score += 10000;
            }
        }
        return score;
    }


    private int getAverageEnemyDistance(Site site) {
        int enemy_count = 0;
        int total_distance = 0;
        int camp = AppUtil.getCurrentArmy(record).getCamp();
        for (Army army : record.getArmyList()) {
            if (army.getCamp() != camp) {
                enemy_count++;
                for (Unit unit : army.getUnits()) {
                    if (!unit.isDead()) {
                        total_distance += AppUtil.getLength(unit, site);
                    }
                }
            }
        }
        return enemy_count == 0 ? 999 : total_distance / enemy_count;

    }

    private int getAverageAllyDistance(Site site) {
        int enemy_count = 0;
        int total_distance = 0;
        int camp = AppUtil.getCurrentArmy(record).getCamp();
        for (Army army : record.getArmyList()) {
            if (army.getCamp() == camp) {
                enemy_count++;
                for (Unit unit : army.getUnits()) {
                    if (!unit.isDead()) {
                        total_distance += AppUtil.getLength(unit, site);
                    }
                }
            }
        }
        return enemy_count == 0 ? 999 : total_distance / enemy_count;

    }

    /**
     * 获取治疗的分数
     *
     * @param tile
     * @param square
     * @return
     */
    public int getTerrainHeal(RegionMes tile, BaseSquare square) {
        int heal = 0;
        if (!abilityList.contains(AbilityEnum.BLOOD_THIRSTY.ability())) {
            if (campColors.contains(square.getColor())) {
                heal += AppUtil.getHpRecover(square);
            }
        }
        if (tile.getType() == RegionEnum.STONE.type() && abilityList.contains(AbilityEnum.HILL_CLOSE.ability())) {
            heal += 10;
        }
        if ((tile.getType() == RegionEnum.GROVE.type() || tile.getType() == RegionEnum.FOREST.type())
                && abilityList.contains(AbilityEnum.HILL_CLOSE.ability())) {
            heal += 10;
        }
        if ((tile.getType().startsWith(RegionEnum.SEA.type()) || (tile.getType().startsWith(RegionEnum.BANK.type()))
                && abilityList.contains(AbilityEnum.FOREST_CLOSE.ability()))) {
            heal += 10;
        }
        return heal;
    }


}
