package pers.mihao.ancient_empire.core.robot;

import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.mihao.ancient_empire.base.bo.*;
import pers.mihao.ancient_empire.base.entity.RegionMes;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.base.enums.AbilityEnum;
import pers.mihao.ancient_empire.base.enums.RegionEnum;
import pers.mihao.ancient_empire.base.enums.StateEnum;
import pers.mihao.ancient_empire.base.enums.UnitEnum;
import pers.mihao.ancient_empire.base.util.AppUtil;
import pers.mihao.ancient_empire.common.util.CollectionUtil;
import pers.mihao.ancient_empire.core.dto.ai.CastleRegion;
import pers.mihao.ancient_empire.core.dto.ai.UnitActionResult;
import pers.mihao.ancient_empire.core.handel.ai.AiMoveHandle;
import pers.mihao.ancient_empire.core.manger.GameContext;
import pers.mihao.ancient_empire.core.manger.strategy.attach.AttachStrategy;
import pers.mihao.ancient_empire.core.util.GameCoreHelper;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 具体选择行动的策略实现类
 *
 * @Author mh32736
 * @Date 2020/9/9 20:37
 */
public class DefaultRobot extends AbstractRobot {

    protected Logger log = LoggerFactory.getLogger(this.getClass());

    public DefaultRobot(GameContext gameContext) {
        super(gameContext);
    }

    @Override
    protected ActionIntention chooseUnitAction(List<Site> moveArea, UnitInfo unit) {

     }

    /**
     * 根据行动选择最佳行动
     *
     * @param actionList
     * @return
     */
    private ActionIntention getPreferredAction(List<ActionIntention> actionList) {
        ActionIntention niceAction = actionList.get(0);
        int maxActionScore = Integer.MIN_VALUE;
        for (ActionIntention actionIntention : actionList) {
            int score = getActionScore(actionIntention);
            if (score > maxActionScore) {
                niceAction = actionIntention;
                maxActionScore = score;
            }
        }
        return niceAction;
    }

    private int getActionScore(ActionIntention action) {
        int score = 0;
        UnitInfo target = action.getAimUnit();
        switch (action.getResultEnum()) {
            case OCCUPIED:
                Region tile = action.getAimRegion();
                if (tile.getType().equals(RegionEnum.CASTLE.type())) {
                    score += 20000;
                }
                if (tile.getType().equals(RegionEnum.TOWN.type())) {
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
                score += 10 * (target.getLevelMes().getMaxAttack() * AppUtil.getUnitLife(target) / 100
                        + target.getLevelMes().getSpeed() * 5);
                break;
            case ATTACH:
                score += target.getUnitMes().getPrice() / 20 + getAttackScore(target);
                break;
            case END:
                if (isThreatened(action.getSite())) {
                    BaseSquare baseSquare = getRegionBySite(action.getSite());
                    if (baseSquare.getType().equals(RegionEnum.CASTLE.type())) {
                        score += 20000;
                    }
                    if (baseSquare.getType().equals(RegionEnum.TOWN.type())) {
                        score -= 10000;
                    }
                }
                break;
            default:
                score += 0;
        }
        if (currUnit().getAbilities().contains(AbilityEnum.ASSAULT.ability())) {
            score += getStandbyScore(action.getSite());
        }
        log.info("行动 {} 评分{}", action.getResultEnum(), score);
        return score;
    }


    /**
     * 根据被攻击单位还获取攻击分数
     *
     * @param beAttach
     * @return
     */
    private int getAttackScore(UnitInfo beAttach) {
        int score = 0;
        int lastLeft = AppUtil.getUnitLife(beAttach);
        int left = AppUtil.getUnitLife(currUnit());
        if (beAttach.getType().equals(UnitEnum.LORD.type())) {
            score += (left - lastLeft) / 10 * beAttach.getUnitMes().getPrice() * beAttach.getLevel();
        } else {
            score += (left - lastLeft) / 20 * beAttach.getUnitMes().getPrice() * beAttach.getLevel();
        }
        if (beAttach.getStatus() == null || StateEnum.NORMAL.type().equals(beAttach.getStatus())) {
            if (beAttach.getAbilities().contains(AbilityEnum.POISONING.ability()) && beAttach.getAbilities()
                    .contains(AbilityEnum.BLINDER.ability())) {
                score += beAttach.getUnitMes().getPrice() / 4;
            }
        } else {
            if (beAttach.getStatus().equals(StateEnum.EXCITED.type())) {
                if (beAttach.getAbilities().contains(AbilityEnum.POISONING.ability()) && beAttach.getAbilities()
                        .contains(AbilityEnum.BLINDER.ability())) {
                    score += beAttach.getUnitMes().getPrice() / 2;
                }
            }

        }
        return score;
    }


    /**
     * 获取待在原地的分数
     *
     * @param site
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

        if (isThreatened(site)) {
            if (square.getType().equals(RegionEnum.CASTLE.type())) {
                score += 20000;
            }
            if (square.getType().equals(RegionEnum.TOWN.type())) {
                score += 10000;
            }
        }
        return score;
    }


    @Override
    protected UnitInfo chooseUnit(List<UnitInfo> unitInfos) {
        Army army = currArmy();
        log.info("{} 色方准备 选择单位", army.getColor());
        UnitInfo choose;
        // 首选有攻击光环的

        if ((choose = getFirstUnitWithAbility(unitInfos, AbilityEnum.ATTACKER)) != null ||
                (choose = getFirstUnitWithAbility(unitInfos, AbilityEnum.PURIFY)) != null ||
                (choose = getFirstUnitWithAbility(unitInfos, AbilityEnum.HEALER)) != null ||
                (choose = getFirstOnCastle(unitInfos)) != null) {
            return choose;
        }
        // 返回剩余的血量多的
        return unitInfos.stream()
                .sorted(Comparator.comparingInt(AppUtil::getUnitLife))
                .findFirst().get();
    }

    @Override
    protected UnitInfo getBestUnit(List<UnitInfo> canBuyUnitMes, NeedUnitType needUnitType) {
        Army army = currArmy();
        // 准备希望要召唤的单位
        if (!AppUtil.hasLoad(army)) {
            log.info("缺少领主 看是否可以购买");
            Optional<UnitInfo> loadUnit = canBuyUnitMes.stream()
                    .filter(unitInfo -> unitInfo.getAbilities().contains(AbilityEnum.CASTLE_GET.ability()))
                    .findAny();
            if (loadUnit.isPresent()) {
                log.info("可以购买领主：{}", loadUnit);
                return loadUnit.get();
            }
        }

        // 获取现在需要的能力
        if (needUnitType.isNeedAbility) {
            log.info("购买单位需要的能力 {}", needUnitType.abilityEnumList);
            List<UnitInfo> buyUnitList = null;
            Stream<UnitInfo> stream;
            for (AbilityEnum abilityEnum : needUnitType.abilityEnumList) {
                stream = canBuyUnitMes.stream()
                        .filter(mes -> mes.getAbilities().contains(abilityEnum.ability()));
                if (stream.count() > 0) {
                    buyUnitList = stream.collect(Collectors.toList());
                    break;
                }
            }

            if (CollectionUtil.isNotEmpty(buyUnitList)) {
                return CollectionUtil.getRandom(buyUnitList);
            }
        } else {
            log.info("购买单位需要的攻击类型 {}： 1 物理 2 魔法", needUnitType.attachType);
            List<UnitInfo> buyUnitList = canBuyUnitMes.stream()
                    .filter(mes -> mes.getUnitMes().getAttackType().equals(needUnitType.attachType))
                    .collect(Collectors.toList());

            return CollectionUtil.getRandom(buyUnitList);
        }
        return null;
    }

    @Override
    protected Map<CastleRegion, Integer> getCastleScore(List<CastleRegion> castleList) {
        Map<CastleRegion, Integer> map = new HashMap<>();
        int camp = record().getCurrCamp();
        record().getArmyList().stream().forEach(army -> {
            if (army.getCamp() != camp) {
                army.getUnits().stream().forEach(unit -> {
                    if (!unit.isDead()) {
                        for (CastleRegion castle : castleList) {
                            Site castleSite = castle.getSite();
                            Integer sum = map.get(castleSite);
                            if (sum == null) {
                                sum = 0;
                            }
                            map.put(castle, sum + AppUtil.getLength(castleSite, unit));
                        }
                    }
                });
            }
        });
        return map;
    }


    /**
     * 选出需要的能力
     *
     * @return
     */
    @Override
    protected NeedUnitType getNeedAbility(UserRecord record) {
        // 首先要有3个具有占领能力的单位
        ArmyUnitSituation situation = getArmyUnitSituation();
        NeedUnitType needUnitType = new NeedUnitType();
        List<AbilityEnum> needAbility = new ArrayList<>();
        if (situation.villageGetNum < 3) {
            needAbility.add(AbilityEnum.VILLAGE_GET);
        } else if (situation.airEnemyNum > situation.shooterNum * 2) {
            needAbility.add(AbilityEnum.SHOOTER);
        } else if (record.getTomb() != null && record.getTomb().size() > situation.summonerNum * 3) {
            needAbility.add(AbilityEnum.SUMMONER);
        } else if (situation.unHealthNum > situation.purifyNum * 3) {
            needAbility.add(AbilityEnum.PURIFY);
        } else if (situation.shooterEnemyNum > situation.remoteDefenseNum * 4) {
            needAbility.add(AbilityEnum.REMOTE_DEFENSE);
        }
        if (needAbility.size() > 0) {
            needUnitType.isNeedAbility = true;
            needUnitType.abilityEnumList = needAbility;
        }
        return new NeedUnitType(false, situation.averagePhyDef > situation.averageMagDef ? PHYSICAL : MAGIC);
    }
}
