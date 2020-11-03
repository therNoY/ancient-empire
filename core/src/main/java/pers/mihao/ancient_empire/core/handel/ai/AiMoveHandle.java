package pers.mihao.ancient_empire.core.handel.ai;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.mihao.ancient_empire.base.bo.Army;
import pers.mihao.ancient_empire.base.bo.BaseSquare;
import pers.mihao.ancient_empire.base.bo.Position;
import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.base.bo.SiteSquare;
import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.entity.Ability;
import pers.mihao.ancient_empire.base.entity.RegionMes;
import pers.mihao.ancient_empire.base.entity.UnitLevelMes;
import pers.mihao.ancient_empire.base.entity.UnitMes;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.base.enums.AbilityEnum;
import pers.mihao.ancient_empire.base.enums.RegionEnum;
import pers.mihao.ancient_empire.base.enums.StateEnum;
import pers.mihao.ancient_empire.base.enums.UnitEnum;
import pers.mihao.ancient_empire.base.service.RegionMesService;
import pers.mihao.ancient_empire.base.service.UnitLevelMesService;
import pers.mihao.ancient_empire.base.util.AppUtil;
import pers.mihao.ancient_empire.common.util.ApplicationContextHolder;
import pers.mihao.ancient_empire.core.dto.ReqUnitIndexDto;
import pers.mihao.ancient_empire.core.dto.ai.ActiveResult;
import pers.mihao.ancient_empire.core.dto.ai.SelectUnitResult;
import pers.mihao.ancient_empire.core.dto.ai.UnitActionResult;
import pers.mihao.ancient_empire.core.eums.ai.AiActiveEnum;
import pers.mihao.ancient_empire.core.manger.GameCoreStaManger;
import pers.mihao.ancient_empire.core.util.GameCoreHelper;
import pers.mihao.ancient_empire.core.websocket.service.WsActionService;
import pers.mihao.ancient_empire.core.websocket.service.WsMoveAreaService;

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
    GameCoreStaManger robotManger;
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
        this.robotManger = GameCoreStaManger.getInstance(record);
        SelectUnitResult selectUnitResult = GameCoreStaManger.getSelectResult(record.getUuid());// 上一次行动的结果
        this.army = record.getArmyList().get(selectUnitResult.getArmyIndex());
        this.selectUnit = army.getUnits().get(selectUnitResult.getUnitIndex());
        this.currSite = AppUtil.getPosition(selectUnit);
        this.abilityList = abilityService.getUnitAbilityListByType(selectUnit.getType());
        this.unitMes = unitMesService.getByType(selectUnit.getType());
        this.campColors = AppUtil.getCampColors(record);
        ReqUnitIndexDto reqUnitIndexDto = new ReqUnitIndexDto(selectUnitResult.getArmyIndex(),
            selectUnitResult.getUnitIndex());
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
            BaseSquare square = GameCoreHelper.getRegionByPosition(record, site);
            List<Position> area = actionService.getAttachArea(unitMes, site, record);
            if (canRepair(square)) {
                log.info("{} 可以进行 修复 操作地点:{}", selectUnit.getType(), site);
                actionList.add(new UnitActionResult(selectUnit, record.getUuid(), AiActiveEnum.REPAIR, site));
            }
            if (canOccupyVillage(square) || canOccupyCastle(square)) {
                log.info("{} 可以进行 占领 操作地点:{}", selectUnit.getType(), site);
                actionList.add(new UnitActionResult(selectUnit, record.getUuid(), AiActiveEnum.OCCUPIED, site));
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
                    actionList.add(new UnitActionResult(selectUnit, record.getUuid(), AiActiveEnum.SUMMON, site));
                }
            }

            // 拥有治疗能力
            if (hasHealer) {
                Unit unit = AppUtil.getUnitByPosition(record, site, army.getCamp());
                if (unit != null && !unit.getStatus().equals(StateEnum.POISON.type())) {
                    if (AppUtil.getUnitLeft(unit) < 100) {
                        log.info("{} 可以进行 治疗 操作目标单位:{} 血量：{}", selectUnit.getType(), unit.getType(),
                            AppUtil.getUnitLeft(unit));
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
                preferredAction.setMoveArea(moveArea);
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

        // 占领 修复的集合
        AnalysisOccAndRep analysisOccAndRep = new AnalysisOccAndRep();
        // 如果有领主能力
        if (abilityList.contains(AbilityEnum.CASTLE_GET.ability())) {
            Site castleSite = getCanBeOccCastle();
            if (castleSite != null) {
                log.info("司令官找到最近的可占领的城堡{}", castleSite);
                analysisOccAndRep.setCastleSite(castleSite);
            }
        }
        // 如果有占领能力
        if (abilityList.contains(AbilityEnum.VILLAGE_GET.ability())) {
            Site targetSite = getCanBeOccVillage();
            if (targetSite != null) {
                log.info("有占领村庄的能力准备选择最近的可占领的村庄{}", targetSite);
                analysisOccAndRep.setVillageSite(targetSite);
            }
        }

        // 如果有修复能力
        if (abilityList.contains(AbilityEnum.REPAIR.ability())) {
            Site targetSite = getCanBeRepVillage();
            if (targetSite != null) {
                log.info("有修复的能力准备选择最近的可修复的村庄{}", targetSite);
                analysisOccAndRep.setRunsSite(targetSite);
            }
        }
        UnitActionResult unitActionResult = analysisOccAndRep.analysis(selectUnit);
        if (unitActionResult != null) {
            unitActionResult.setMoveArea(moveArea);
            return unitActionResult;
        }

        Unit enemyLoad;
        if ((enemyLoad = getNearestEnemyCommander()) != null) {
            log.info("找到最近的敌军指挥官 准备并向他移动");
            Site site = getNextPositionToTarget(AppUtil.getPosition(enemyLoad));
            return new UnitActionResult(record.getUuid(), AiActiveEnum.MOVE_UNIT, site, selectUnit, moveArea);
        } else {
            log.info("没有找到最近的敌军指挥官");
            Unit nearestEnemy;
            if ((nearestEnemy = getNearestEnemy()) != null) {
                Site nextPosition = getNextPositionToTarget(AppUtil.getPosition(nearestEnemy));
                log.info("普通单位找到最近的可攻击的单位{}", nextPosition);
                return new UnitActionResult(record.getUuid(), AiActiveEnum.MOVE_UNIT, nextPosition, selectUnit,
                    moveArea);
            } else {
                Site niceSite = getPreferredStandbyPosition();
                log.info("没有可攻击的单位找到最好的移动地点{}", niceSite);
                return new UnitActionResult(record.getUuid(), AiActiveEnum.MOVE_UNIT, niceSite, selectUnit, moveArea);
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
                BaseSquare tile = GameCoreHelper.getRegionByPosition(record, action.getSite());
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
                UnitLevelMes targetUnitLevelMes = unitLevelMesService
                    .getUnitLevelMes(target.getType(), target.getLevel());
                score += 10 * (targetUnitLevelMes.getMaxAttack() * AppUtil.getUnitLeft(target) / 100
                    + targetUnitLevelMes.getSpeed() * 5);
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
            BaseSquare baseSquare = GameCoreHelper.getRegionByPosition(record, currSite);
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
            if (!army.getCamp().equals(this.army.getCamp())) {
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
            if (abilityList.contains(AbilityEnum.POISONING.ability()) && abilityList
                .contains(AbilityEnum.BLINDER.ability())) {
                score += targetUnitMes.getPrice() / 4;
            }
        } else {
            if (defender.getStatus().equals(StateEnum.EXCITED.type())) {
                if (abilityList.contains(AbilityEnum.POISONING.ability()) && abilityList
                    .contains(AbilityEnum.BLINDER.ability())) {
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

            if (!army.getCamp().equals(this.army.getCamp())) {
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
        List<Site> aimSite = robotManger.getAimSite().get(army.getId());
        int minDistance = Integer.MAX_VALUE;
        for (SiteSquare siteSquare : robotManger.getCastleSite()) {
            if (!campColors.contains(siteSquare.getSquare().getColor())) {
                if (aimSite == null || !aimSite.contains(siteSquare.getSite())) {
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
     * 获取一个最近的可被修复的点
     *
     * @return
     */
    private Site getCanBeRepVillage() {
        Site targetSite = null;
        List<Site> aimSite = robotManger.getAimSite().get(army.getId());
        int minDistance = Integer.MAX_VALUE;
        for (SiteSquare siteSquare : robotManger.getRuinsSite()) {
            if (aimSite == null || !aimSite.contains(siteSquare.getSite())) {
                int distance = AppUtil.getLength(siteSquare.getSite(), AppUtil.getPosition(selectUnit));
                if (distance < minDistance) {
                    targetSite = siteSquare.getSite();
                    minDistance = distance;
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
        List<Site> aimSite = robotManger.getAimSite().get(army.getId());
        int minDistance = Integer.MAX_VALUE;
        for (SiteSquare siteSquare : robotManger.getVillageSite()) {
            if (!campColors.contains(siteSquare.getSquare().getColor())) {
                if (aimSite == null || !aimSite.contains(siteSquare.getSite())) {
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
        BaseSquare square = GameCoreHelper.getRegionByPosition(record, site);
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
                heal += GameCoreHelper.getHpRecover(square);
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

    private class AnalysisOccAndRep {

        Site castleSite;
        Site villageSite;
        Site runsSite;
        Site targetSite;
        int castleScore;
        int villageScore;
        int runsScore;

        public void setCastleSite(Site castleSite) {
            this.castleSite = castleSite;
        }

        public void setVillageSite(Site villageSite) {
            this.villageSite = villageSite;
        }

        public void setRunsSite(Site runsSite) {
            this.runsSite = runsSite;
        }

        /**
         * 选择最好的占领目标
         *
         * @param unit
         * @return
         */
        public UnitActionResult analysis(Unit unit) {
            if (castleSite != null) {
                castleScore = AppUtil.getLength(unit, castleSite) - 2;
            }
            if (villageSite != null) {
                villageScore = AppUtil.getLength(unit, villageSite) - 1;
            }
            if (runsSite != null) {
                runsScore = AppUtil.getLength(unit, runsSite);
            }

            if (castleScore == villageScore && villageScore == runsScore && villageScore == 0) {
                return null;
            }

            if (castleScore >= villageScore) {
                if (castleScore >= runsScore) {
                    // 选择占领城堡
                    log.info("目标 占领城堡：{}", castleSite);
                    targetSite = castleSite;
                } else {
                    // 选择修复废墟
                    log.info("目标 修复废墟：{}", runsSite);
                    targetSite = runsSite;
                }
            } else {
                if (villageScore >= runsScore) {
                    // 选择占领城堡
                    log.info("目标 占领城镇：{}", villageSite);
                    targetSite = villageSite;
                } else {
                    log.info("目标 修复废墟：{}", runsSite);
                    targetSite = runsSite;
                }
            }

            Site site = getNextPositionToTarget(targetSite);
            List<Site> sites = robotManger.getAimSite().get(army.getId());
            if (sites == null) {
                sites = new ArrayList<>();
                sites.add(targetSite);
                robotManger.getAimSite().put(army.getId(), sites);
            } else {
                sites.add(targetSite);
            }

            return new UnitActionResult(record.getUuid(), AiActiveEnum.MOVE_UNIT, site, selectUnit, moveArea);
        }


    }
}
