package pers.mihao.ancient_empire.core.robot;

import java.util.List;
import pers.mihao.ancient_empire.base.bo.Army;
import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.base.enums.AbilityEnum;
import pers.mihao.ancient_empire.core.dto.ai.BuyUnitResult;
import pers.mihao.ancient_empire.core.dto.ai.EndTurnResult;
import pers.mihao.ancient_empire.core.manger.GameContext;

/**
 * 具体选择行动的策略实现类
 * @Author mh32736
 * @Date 2020/9/9 20:37
 */
public class DefaultRoot extends Robot{

    public DefaultRoot(GameContext gameContext) {
        super(gameContext);
    }

    @Override
    protected ActionIntention chooseUnitAction(GameContext context, Unit unit) {
        return null;
    }

    @Override
    protected Unit chooseUnit(List<Unit> moreThanHalf) {
        UserRecord record = gameContext.getUserRecord();
        Army army = record.getArmyList().get(record.getCurrArmyIndex());
        log.info("{} 色方准备 选择单位", army.getColor());
        // 首选有攻击光环的
        Unit attacker = getFirstUnitWithAbility(army, AbilityEnum.ATTACKER);
        if (attacker != null) {
            selectUnitResult.setSite(AppUtil.getPosition(attacker));
            selectUnitResult.setUnitIndex(AppUtil.getUnitIndex(attacker, army));
            log.info("选择标准：攻击光环");
            return selectUnitResult;
        }

        // 然后选择净化光环的
        Unit purify = getFirstUnitWithAbility(army, AbilityEnum.PURIFY);
        if (purify != null) {
            selectUnitResult.setSite(AppUtil.getPosition(purify));
            selectUnitResult.setUnitIndex(AppUtil.getUnitIndex(purify, army));
            log.info("选择标准 ： 净化光环");
            return selectUnitResult;
        }

        // 然后选择治愈光环的
        Unit healer = getFirstUnitWithAbility(AppUtil.getCurrentArmy(record), AbilityEnum.HEALER);
        if (healer != null) {
            selectUnitResult.setSite(AppUtil.getPosition(healer));
            selectUnitResult.setUnitIndex(AppUtil.getUnitIndex(healer, army));
            log.info("选择标准 ：治愈能力");
            return selectUnitResult;
        }

        // 然后选择位置是城堡的
        Unit onCastleUnit = getFirstOnCastle(record, army);
        if (onCastleUnit != null) {
            selectUnitResult.setSite(AppUtil.getPosition(onCastleUnit));
            selectUnitResult.setUnitIndex(AppUtil.getUnitIndex(onCastleUnit, army));
            log.info("选择标准 ：城堡之上");
            return selectUnitResult;
        }

        // 返回剩余的血量多的
        Unit unit = getLifeHealth(army);
        if (unit != null) {
            selectUnitResult.setSite(AppUtil.getPosition(unit));
            selectUnitResult.setUnitIndex(AppUtil.getUnitIndex(unit, army));
            log.info("选择标准 ：血量较多");
            return selectUnitResult;
        }

        // 没有可选的的单位  查看是否可以进行购买
        BuyUnitResult result = getBuyUnitResult(record);
        if (result != null) {
            return result;
        }

        log.info("{} 色方准备结束回合", army.getColor());
        return new EndTurnResult(recordId);
        return null;
    }

    @Override
    protected Unit buyNewUnit(UserRecord userRecord) {
        return null;
    }


    /**
     * 获取第一个拥有什么能力的人
     *
     * @param army
     * @param ability
     * @return
     */
    private Unit getFirstUnitWithAbility(Army army, AbilityEnum ability) {
        for (Unit unit : army.getUnits()) {
            if (!unit.isDone() && !unit.isDead() && abilityService.getUnitAbilityListByType(unit.getType()).contains(ability.type())) {
                return unit;
            }
        }
        return null;
    }
}
