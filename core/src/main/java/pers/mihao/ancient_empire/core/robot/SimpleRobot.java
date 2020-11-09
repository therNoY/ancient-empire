package pers.mihao.ancient_empire.core.robot;

import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.bo.UnitInfo;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.core.dto.ai.CastleRegion;
import pers.mihao.ancient_empire.core.manger.GameContext;
import pers.mihao.ancient_empire.core.manger.strategy.action.ActionStrategy;

import java.util.List;
import java.util.Map;

/**
 * @version 1.0
 * @auther mihao
 * @date 2020\11\8 0008 15:46
 */
public class SimpleRobot extends AbstractRobot{

    public SimpleRobot(GameContext gameContext) {
        super(gameContext);
    }

    @Override
    protected ActionIntention chooseUnitAction(GameContext context, Unit unit) {
        return null;
    }

    @Override
    protected UnitInfo chooseUnit(List<UnitInfo> moreThanHalf) {
        return moreThanHalf.get(0);
    }

    @Override
    protected UnitInfo getBestUnit(List<UnitInfo> canBuyUnitMes, NeedUnitType needUnitType) {
        return null;
    }

    @Override
    protected Map<CastleRegion, Integer> getCastleScore(List<CastleRegion> castleList) {
        return null;
    }

    @Override
    protected NeedUnitType getNeedAbility(UserRecord userRecord) {
        return null;
    }
}
