package pers.mihao.ancient_empire.core.robot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.mihao.ancient_empire.base.bo.Army;
import pers.mihao.ancient_empire.base.bo.Region;
import pers.mihao.ancient_empire.base.bo.RegionInfo;
import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.bo.UnitInfo;
import pers.mihao.ancient_empire.base.entity.Ability;
import pers.mihao.ancient_empire.base.entity.UnitLevelMes;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.base.enums.AbilityEnum;
import pers.mihao.ancient_empire.base.enums.RegionEnum;
import pers.mihao.ancient_empire.base.enums.StateEnum;
import pers.mihao.ancient_empire.common.util.ApplicationContextHolder;
import pers.mihao.ancient_empire.common.util.EnumUtil;
import pers.mihao.ancient_empire.core.eums.GameCommendEnum;
import pers.mihao.ancient_empire.core.eums.GameEventEnum;
import pers.mihao.ancient_empire.core.eums.SendTypeEnum;
import pers.mihao.ancient_empire.core.manger.GameCoreManger;
import pers.mihao.ancient_empire.core.manger.net.GameSessionManger;
import pers.mihao.ancient_empire.core.manger.command.GameCommand;
import pers.mihao.ancient_empire.core.manger.event.GameEvent;
import pers.mihao.ancient_empire.core.manger.handler.CommonHandler;
import pers.mihao.ancient_empire.core.manger.strategy.attach.AttachStrategy;
import pers.mihao.ancient_empire.core.manger.strategy.move_area.MoveAreaStrategy;

/**
 * 分析当前游戏
 *
 * @version 1.0
 * @author mihao
 * @date 2020\11\8 0008 15:38
 */
public abstract class RobotCommonHandler extends CommonHandler {

    private Logger log = LoggerFactory.getLogger(RobotCommonHandler.class);

    private List<RegionInfo> threatenedRegion;

    protected static GameCoreManger gameCoreManger;
    protected static GameSessionManger gameSessionManger;

    static {
        gameCoreManger = ApplicationContextHolder.getBean(GameCoreManger.class);
        gameSessionManger = ApplicationContextHolder.getBean(GameSessionManger.class);
    }

    /**
     * 统计军队的状况
     *
     * @return
     */
    protected ArmyUnitSituation getArmyUnitSituation() {
        ArmyUnitSituation armyUnitSituation = new ArmyUnitSituation();
        UserRecord record = gameContext.getUserRecord();
        List<Army> armies = record.getArmyList();
        int camp = record.getCurrCamp();
        // 统计军队状况
        for (Army army : armies) {
            for (Unit unit : army.getUnits()) {
                if (army.getCamp() == camp) {
                    // 统计己方能力数量
                    List<Ability> abilityList = abilityService.getUnitAbilityList(unit.getTypeId());
                    for (Ability ability : abilityList) {
                        AbilityEnum abilityEnum = EnumUtil.valueOf(AbilityEnum.class, ability.getType());
                        switch (abilityEnum) {
                            case VILLAGE_GET:
                                armyUnitSituation.villageGetNum++;
                                break;
                            case PURIFY:
                                armyUnitSituation.purifyNum++;
                                break;
                            case SUMMONER:
                                armyUnitSituation.summonerNum++;
                                break;
                            case SHOOTER:
                                armyUnitSituation.shooterNum++;
                                break;
                            case REPAIR:
                                armyUnitSituation.repairerNum++;
                                break;
                            default:
                                break;
                        }

                    }

                    // 统计己方不健康数量
                    String statue = unit.getStatus();
                    if (statue != null && !statue.equals(StateEnum.EXCITED.type()) && !statue
                        .equals(StateEnum.EXCITED.type())) {
                        armyUnitSituation.unHealthNum++;
                    }
                } else {
                    // 统计敌方情概况
                    armyUnitSituation.enemyNum++;
                    UnitLevelMes levelMes = unitMesService.getUnitInfo(unit.getTypeId(), unit.getLevel()).getLevelMes();
                    armyUnitSituation.enemyPhyDefSum = levelMes.getPhysicalDefense();
                    armyUnitSituation.enemyMagDefSum = levelMes.getMagicDefense();
                    List<Ability> abilityList = abilityService.getUnitAbilityList(unit.getTypeId());
                    if (abilityList.contains(AbilityEnum.FLY.ability())) {
                        armyUnitSituation.airEnemyNum++;
                    } else if (abilityList.contains(AbilityEnum.SHOOTER.ability())) {
                        armyUnitSituation.shooterEnemyNum++;
                    }
                }
            }
        }

        armyUnitSituation.averagePhyDef = armyUnitSituation.enemyPhyDefSum / armyUnitSituation.enemyNum;
        armyUnitSituation.averageMagDef = armyUnitSituation.enemyMagDefSum / armyUnitSituation.enemyNum;
        return armyUnitSituation;
    }

    /**
     * 获取当前 单位的能力
     *
     * @return
     */
    protected UnitAble getUnitAble() {
        List<Ability> abilities = currUnit().getAbilities();
        UnitAble unitAble = new UnitAble(currUnit());
        AbilityEnum abilityEnum;
        for (Ability ability : abilities) {
            abilityEnum = EnumUtil.valueOf(AbilityEnum.class, ability.getType());
            switch (abilityEnum) {
                case CASTLE_GET:
                    unitAble.castleGet = true;
                    break;
                case REPAIR:
                    unitAble.repairer = true;
                    break;
                case SUMMONER:
                    unitAble.hasSummoner = true;
                    break;
                case VILLAGE_GET:
                    unitAble.villageGet = true;
                    break;
                case HEALER:
                    unitAble.hasHealer = true;
                    break;
                default:
                    break;
            }
        }
        return unitAble;
    }

    /**
     * 需要的单位能力
     */
    protected static class NeedUnitType {

        /**
         * 是否需要某种能力
         */
        boolean isNeedAbility;
        List<AbilityEnum> abilityEnumList;
        String attachType;

        public NeedUnitType() {
        }


        public NeedUnitType(boolean isNeedAbility, String attachType) {
            this.isNeedAbility = isNeedAbility;
            this.attachType = attachType;
        }
    }


    /**
     * 获取第一个拥有什么能力的单位
     *
     * @param ability
     * @param ability
     * @return
     */
    protected UnitInfo getFirstUnitWithAbility(List<UnitInfo> unitInfos, AbilityEnum ability) {
        for (UnitInfo unitInfo : unitInfos) {
            if (!unitInfo.isDone() && !unitInfo.isDead() && unitInfo.getAbilities().contains(ability.ability())) {
                return unitInfo;
            }
        }
        return null;
    }

    /**
     * 获取位置是城堡的单位
     *
     * @param unitInfos
     * @return
     */
    protected UnitInfo getFirstOnCastle(List<UnitInfo> unitInfos) {
        for (UnitInfo unit : unitInfos) {
            if (!unit.isDone() && !unit.isDead()) {
                RegionInfo regionInfo = unit.getRegionInfo();
                if (regionInfo.getType().equals(RegionEnum.CASTLE.type())) {
                    return unit;
                }
            }
        }
        return null;
    }


    protected boolean isThreatened(Site site) {
        return threatenedRegion.contains(site);
    }

    public List<RegionInfo> getThreatenedRegion() {
        return threatenedRegion;
    }

    public void setThreatenedRegion(List<RegionInfo> threatenedRegion) {
        this.threatenedRegion = threatenedRegion;
    }

    protected List<UnitInfo> getAllFriendUnits() {
        List<UnitInfo> friend = new ArrayList<>();
        for (Army army : record().getArmyList()) {
            if (army.getCamp().equals(currArmy().getCamp())) {
                for (Unit unit : army.getUnits()) {
                    friend.add(getUnitInfoByUnit(unit));
                }
            }
        }
        return friend;
    }

    protected List<UnitInfo> getAllEnemyUnits() {
        List<UnitInfo> enemys = new ArrayList<>();
        for (Army army : record().getArmyList()) {
            if (!army.getCamp().equals(currArmy().getCamp())) {
                for (Unit unit : army.getUnits()) {
                    enemys.add(getUnitInfoByUnit(unit));
                }
            }
        }
        return enemys;
    }

    /**
     * 获取所有的可以修复的区域
     * @return
     */
    protected List<RegionInfo> getAllCanRepairRegion() {
        return getAllSiteByType(RegionEnum.RUINS).stream()
                .filter(regionInfo -> !isHaveUnit(record(), regionInfo.getRow(), regionInfo.getColumn()))
                .collect(Collectors.toList());
    }

    /**
     * 获取所有的可以占领的城镇
     * @return
     */
    protected List<RegionInfo> getAllCanOccupyVillage() {
        return getAllSiteByType(RegionEnum.TOWN).stream()
                .filter(regionInfo -> !colorIsCamp(regionInfo.getColor()) && !isHaveUnit(record(), regionInfo.getRow(), regionInfo.getColumn()))
                .collect(Collectors.toList());
    }

    /**
     * 获取所有的可以占领的城堡
     * @return
     */
    protected List<RegionInfo> getAllCanOccupyCastle() {
        return getAllSiteByType(RegionEnum.CASTLE).stream()
                .filter(regionInfo -> !colorIsCamp(regionInfo.getColor()) && !isHaveUnit(record(), regionInfo.getRow(), regionInfo.getColumn()))
                .collect(Collectors.toList());
    }

    private List<RegionInfo> getAllSiteByType(RegionEnum regionEnum){
        List<RegionInfo> regionInfos = new ArrayList<>();
        for (int i = 0; i < gameMap().getRegions().size(); i++) {
            Region region = gameMap().getRegions().get(i);
            if (region.getType().equals(regionEnum.type())) {
                regionInfos.add(getRegionInfoByRegionIndex(i));
            }
        }
        return regionInfos;
    }

    /**
     * 根据移动区域获取单位的攻击区域
     * @param moveArea
     * @return
     */
    protected List<Site> getUnitAttachArea(List<Site> moveArea) {
        List<Site> pointAttachArea;
        Set<Site> unitAttachArea = new HashSet<>();
        for (Site site : moveArea) {
            pointAttachArea = AttachStrategy.getInstance().getAttachArea(currUnit().getUnitMes(), site, gameMap());
            unitAttachArea.addAll(pointAttachArea);
        }
        return unitAttachArea.stream().distinct().collect(Collectors.toList());
    }

    /**
     * 获取单位的移动区域
     * @return
     */
    protected List<Site> getUnitMoveArea() {
        return MoveAreaStrategy.getInstance().getMoveArea(record(), currUnit());
    }

    /**
     * 军队的单位状况
     */
    static class ArmyUnitSituation {

        /**
         * 可以修改的数量
         */
        int repairerNum = 0;

        /**
         * 可以占领的单位状况
         */
        int villageGetNum = 0;
        /**
         * 净化者的数量
         */
        int purifyNum = 0;
        /**
         * 召唤着的数量
         */
        int summonerNum = 0;
        /**
         * 弓手的数量
         */
        int shooterNum = 0;
        /**
         * 不健康的数量
         */
        int unHealthNum = 0;
        /**
         * 远程防御的数量
         */
        int remoteDefenseNum = 0;

        /**
         * 敌军的数量
         */
        int enemyNum = 0;
        /**
         * 敌军的物理防御
         */
        int enemyPhyDefSum = 0;
        /**
         * 敌军的魔法防御
         */
        int enemyMagDefSum = 0;
        /**
         * 地方平均物理防御
         */
        int averagePhyDef = 0;
        /**
         * 平均魔法防御
         */
        int averageMagDef = 0;
        /**
         * 空军的数量
         */
        int airEnemyNum = 0;
        /**
         * 敌方弓手的数量
         */
        int shooterEnemyNum = 0;
    }

    static class CanMoveUnit {

        List<UnitInfo> moreThanHalf;
        List<UnitInfo> lessThanHalf;

        @Override
        public String toString() {
            return "CanMoveUnit{" +
                "moreThanHalf=" + moreThanHalf.stream().map(unitInfo -> unitInfo.simpleInfoShow()).collect(Collectors.toList()) +
                ", lessThanHalf=" + lessThanHalf.stream().map(unitInfo -> unitInfo.simpleInfoShow()).collect(Collectors.toList()) +
                '}';
        }
    }

    static class UnitAble {

        UnitInfo unit;

        boolean hasSummoner;
        boolean hasHealer;
        boolean castleGet;
        boolean villageGet;
        boolean repairer;

        public UnitAble(UnitInfo unit) {
            this.unit = unit;
            hasSummoner = unit.getAbilities().contains(AbilityEnum.SUMMONER.ability());
            hasHealer = unit.getAbilities().contains(AbilityEnum.HEALER.ability());
            castleGet = unit.getAbilities().contains(AbilityEnum.CASTLE_GET.ability());
            villageGet = unit.getAbilities().contains(AbilityEnum.VILLAGE_GET.ability());
            repairer = unit.getAbilities().contains(AbilityEnum.REPAIR.ability());
        }
    }

    /**
     * 生成机器人处理事件
     * @param eventEnum
     * @param initiateSite
     */
    protected void handleRobotEvent(GameEventEnum eventEnum, Site initiateSite){
        GameEvent event = new GameEvent();
        event.setId(gameContext.getGameId());
        event.setEvent(eventEnum);
        event.setInitiateSite(initiateSite);
        handleRobotEvent(event);
    }

    /**
     * 生成机器人处理事件
     * @param eventEnum
     * @param initiateSite
     */
    protected void handleRobotEvent(GameEventEnum eventEnum, Site initiateSite, Site aimSite){
        GameEvent event = new GameEvent();
        event.setId(gameContext.getGameId());
        event.setEvent(eventEnum);
        event.setInitiateSite(initiateSite);
        event.setAimSite(aimSite);
        handleRobotEvent(event);
    }

    /**
     * 处理机器人事件
     */
    protected void handleRobotEvent(GameEventEnum gameEventEnum, Integer unitId) {
        GameEvent gameEvent = new GameEvent();
        gameEvent.setEvent(gameEventEnum);
        gameEvent.setId(gameContext.getGameId());
        gameEvent.setUnitId(unitId);
        handleRobotEvent(gameEvent);
    }


    protected void handleRobotEvent(GameEventEnum gameEventEnum){
        GameEvent gameEvent = new GameEvent();
        gameEvent.setEvent(gameEventEnum);
        gameEvent.setId(gameContext.getGameId());
        handleRobotEvent(gameEvent);
    }

    protected void handleRobotEvent(GameEvent event){
        gameCoreManger.handelTask(event);
        int wait = RobotWaitTimeCatch.getInstance().getLockTimeByEvent(event.getEvent());
        if (gameContext.getInteractiveLock().isExecutionIng()) {
            log.info("发送命令：{}完成 准备lock:{}ms之后重新运行", event, wait);
            gameContext.getInteractiveLock().untilExecutionOk(3000, wait);
            log.info("休眠完成 准备重新执行任务");
        } else {
            try {
                Thread.sleep(wait);
            } catch (InterruptedException e) {
                log.error("", e);
            }
        }

    }
}
