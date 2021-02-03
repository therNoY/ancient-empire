package pers.mihao.ancient_empire.core.manger.strategy.start;

import pers.mihao.ancient_empire.base.entity.RegionMes;
import pers.mihao.ancient_empire.base.enums.AbilityEnum;
import pers.mihao.ancient_empire.base.enums.RegionEnum;
import pers.mihao.ancient_empire.core.util.GameCoreHelper;

/**
 * @Author mh32736
 * @Date 2021/2/3 12:49
 */
public class HillCloseStartStrategy extends StartStrategy{

    @Override
    protected int recoverLife(RegionMes regionMes) {
        if (regionMes.getType().equals(RegionEnum.GROVE.type())) {
            return GameCoreHelper.getContext().getRecoverByAbility(AbilityEnum.HILL_CLOSE);
        }
        return 0;
    }

}
