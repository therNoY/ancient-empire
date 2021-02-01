package pers.mihao.ancient_empire.core.robot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.mihao.ancient_empire.base.bo.Army;
import pers.mihao.ancient_empire.base.bo.BaseSquare;
import pers.mihao.ancient_empire.base.bo.RegionInfo;
import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.bo.UnitInfo;
import pers.mihao.ancient_empire.base.enums.AbilityEnum;
import pers.mihao.ancient_empire.base.enums.RegionEnum;
import pers.mihao.ancient_empire.base.enums.StateEnum;
import pers.mihao.ancient_empire.base.enums.UnitEnum;
import pers.mihao.ancient_empire.base.util.AppUtil;
import pers.mihao.ancient_empire.common.util.CollectionUtil;
import pers.mihao.ancient_empire.core.dto.ai.CastleRegion;
import pers.mihao.ancient_empire.core.manger.GameContext;

/**
 * 具体选择行动的策略实现类
 *
 * @Author mh32736
 * @Date 2020/9/9 20:37
 */
public class DefaultRobot extends AbstractRobot {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public DefaultRobot(GameContext gameContext) {
        super(gameContext);
    }

    /**
     * 获取行动得分
     *
     * @param action
     * @return
     */
    @Override
    protected int getActionScore(ActionIntention action) {
        int score = 0;
        UnitInfo target = action.getAimUnit();
        switch (action.getResultEnum()) {
            case OCCUPIED:
                RegionInfo tile = action.getAimRegion();
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
                score += 10 * (target.getLevelMes().getMaxAttack() * target.getLife() / 100
                    + target.getLevelMes().getSpeed() * 5);
                break;
            case ATTACH:
                score += target.getUnitMes().getPrice() / 20 + getAttackScore(target);
                break;
            case DEFENSIVE:
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
        log.info("行动 {} 评分{}", action.getResultEnum(), score);
        return score;
    }

    @Override
    protected UnitInfo chooseUnit(List<UnitInfo> unitInfos) {
        Army army = currArmy();
        log.info("{} 色方准备 选择单位", army.getColor());
        UnitInfo choose;

        // 首选根据能力选择 有攻击光环的 然后选择净化光环 然后治疗 然后城堡之上
        if ((choose = getFirstUnitWithAbility(unitInfos, AbilityEnum.ATTACKER)) != null ||
            (choose = getFirstUnitWithAbility(unitInfos, AbilityEnum.PURIFY)) != null ||
            (choose = getFirstUnitWithAbility(unitInfos, AbilityEnum.HEALER)) != null ||
            (choose = getFirstOnCastle(unitInfos)) != null) {
            return choose;
        }
        // 返回剩余的血量多的
        return unitInfos.stream()
            .sorted(Comparator.comparingInt(Unit::getLife))
            .findFirst().get();
    }

    @Override
    protected UnitInfo getMastNeedUnit(List<UnitInfo> canBuyUnitMes, NeedUnitType needUnitType) {
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
            java.util.stream.Stream<UnitInfo> stream;
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
    protected Long getCastleScore(CastleRegion castle) {
        long sum = 0;
        int camp = record().getCurrCamp();
        for (Army army : record().getArmyList()) {
            if (army.getCamp() != camp) {
                for (Unit unit : army.getUnits()) {
                    if (!unit.isDead()) {
                        Site castleSite = castle.getSite();
                        sum += AppUtil.getLength(castleSite, unit);
                    }
                }
            }
        }
        return sum;
    }

    /**
     * 选出当前需要的能力
     *
     * @return
     */
    @Override
    protected NeedUnitType getNeedAbility() {
        // 首先要有3个具有占领能力的单位
        ArmyUnitSituation situation = getArmyUnitSituation();
        NeedUnitType needUnitType = new NeedUnitType();
        List<AbilityEnum> needAbility = new ArrayList<>();
        if (situation.villageGetNum < 3) {
            needAbility.add(AbilityEnum.VILLAGE_GET);
        } else if (situation.airEnemyNum > situation.shooterNum * 2) {
            needAbility.add(AbilityEnum.SHOOTER);
        } else if (record().getTombList() != null && record().getTombList().size() > situation.summonerNum * 3) {
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

    /**
     * 根据被攻击单位还获取攻击分数
     *
     * @param beAttach
     * @return
     */
    private int getAttackScore(UnitInfo beAttach) {
        int score = 0;
        int lastLeft = beAttach.getLife();
        int left = currUnit().getLife();
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
}
