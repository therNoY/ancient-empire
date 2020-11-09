package pers.mihao.ancient_empire.core.robot;

import pers.mihao.ancient_empire.base.bo.*;
import pers.mihao.ancient_empire.base.entity.Ability;
import pers.mihao.ancient_empire.base.entity.UnitLevelMes;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.base.enums.AbilityEnum;
import pers.mihao.ancient_empire.base.enums.RegionEnum;
import pers.mihao.ancient_empire.base.enums.StateEnum;
import pers.mihao.ancient_empire.common.util.ApplicationContextHolder;
import pers.mihao.ancient_empire.common.util.EnumUtil;
import pers.mihao.ancient_empire.core.manger.GameContextBaseHandler;
import pers.mihao.ancient_empire.core.manger.GameCoreManger;

import java.util.List;

/**
 * 分析当前游戏
 *
 * @version 1.0
 * @auther mihao
 * @date 2020\11\8 0008 15:38
 */
public abstract class GameAnalysis extends GameContextBaseHandler {

    protected static GameCoreManger gameCoreManger;

    static {
        gameCoreManger = ApplicationContextHolder.getBean(GameCoreManger.class);
    }

    private List<Site> threatenedRegion;


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
                    List<Ability> abilityList = abilityService.getUnitAbilityListByType(unit.getType());
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
                        }

                    }

                    // 统计己方不健康数量
                    String statue = unit.getStatus();
                    if (statue != null && !statue.equals(StateEnum.EXCITED.type()) && !statue.equals(StateEnum.EXCITED.type())) {
                        armyUnitSituation.unHealthNum++;
                    }
                } else {
                    // 统计敌方情概况
                    armyUnitSituation.enemyNum++;
                    UnitLevelMes levelMes = unitMesService.getUnitInfo(unit.getType(), unit.getLevel()).getLevelMes();
                    armyUnitSituation.enemyPhyDefSum = levelMes.getPhysicalDefense();
                    armyUnitSituation.enemyMagDefSum = levelMes.getMagicDefense();
                    List<Ability> abilityList = abilityService.getUnitAbilityListByType(unit.getType());
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
     * 需要的单位能力
     */
    protected static class NeedUnitType {
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


    protected static class ArmyUnitSituation {
        int villageGetNum = 0;
        int purifyNum = 0;
        int summonerNum = 0;
        int shooterNum = 0;
        int unHealthNum = 0;
        int remoteDefenseNum = 0;

        int enemyNum = 0;
        int enemyPhyDefSum = 0;
        int enemyMagDefSum = 0;
        /**
         * 地方平均物理防御
         */
        int averagePhyDef = 0;
        /**
         * 平均魔法防御
         */
        int averageMagDef = 0;
        int airEnemyNum = 0;
        int shooterEnemyNum = 0;
    }


    /**
     * 获取第一个拥有什么能力的单位
     *
     * @param army
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
     * @param army
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

    /**
     * 判断单位是否可以修复
     *
     * @param square
     * @return
     */
    protected boolean canRepair(Region square) {
        if (square.getType().equals(RegionEnum.RUINS.type())) {
            return unit.getAbilities().contains(AbilityEnum.REPAIR.ability());
        }
        return false;
    }

    /**
     * 是否可以占领
     *
     * @param square
     * @return
     */
    protected boolean canOccupyVillage(Region square) {
        if (square.getType().equals(RegionEnum.TOWN.type()) &&
                !record().getCurrColor().contains(square.getColor())) {
            return unit.getAbilities().contains(AbilityEnum.VILLAGE_GET.ability());
        }
        return false;
    }

    /**
     * 是否可以占领
     *
     * @param square
     * @return
     */
    protected boolean canOccupyCastle(BaseSquare square) {
        if (unit.getAbilities().contains(AbilityEnum.CASTLE_GET.ability())) {
            if (square.getType().equals(RegionEnum.CASTLE.type())
                    && !record().getCurrColor().contains(square.getColor())) {
                return true;
            }
        }
        return false;
    }

    protected boolean isThreatened(Site site) {
        return threatenedRegion.contains(site);
    }

    protected List<Site> getThreatened() {
        return threatenedRegion;
    }

    protected List<UnitInfo> getAllFriendUnits() {
    }

    protected List<UnitInfo> getAllEnemyUnits() {
    }

    protected List<RegionInfo> getAllCanRepairRegion() {

    }

    protected List<RegionInfo> getAllCanOccupyVillage() {

    }

    protected List<RegionInfo> getAllCanOccupyCastle() {

    }


    static class CanMoveUnit {
        List<UnitInfo> moreThanHalf;
        List<UnitInfo> lessThanHalf;

        @Override
        public String toString() {
            return "CanMoveUnit{" +
                    "moreThanHalf=" + moreThanHalf +
                    ", lessThanHalf=" + lessThanHalf +
                    '}';
        }
    }

    static class UnitAble {

        UnitInfo unit;

        boolean hasSummoner;
        boolean hasHealer;
        boolean castleGeteer;
        boolean villageGeteer;
        boolean repairer;

        {
            hasSummoner = unit.getAbilities().contains(AbilityEnum.SUMMONER.ability());
            hasHealer = unit.getAbilities().contains(AbilityEnum.HEALER.ability());
            castleGeteer = unit.getAbilities().contains(AbilityEnum.CASTLE_GET.ability());
            villageGeteer = unit.getAbilities().contains(AbilityEnum.VILLAGE_GET.ability());
            repairer = unit.getAbilities().contains(AbilityEnum.REPAIR.ability());
        }

    }

}
