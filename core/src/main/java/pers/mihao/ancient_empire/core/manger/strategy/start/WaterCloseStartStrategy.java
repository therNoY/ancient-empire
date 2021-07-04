package pers.mihao.ancient_empire.core.manger.strategy.start;

import pers.mihao.ancient_empire.base.entity.RegionMes;
import pers.mihao.ancient_empire.base.enums.AbilityEnum;
import pers.mihao.ancient_empire.base.enums.RegionEnum;
import pers.mihao.ancient_empire.core.util.GameCoreHelper;

/**
 * 水之子 回学
 * @Author mihao
 * @Date 2021/2/3 12:49
 */
public class WaterCloseStartStrategy extends StartStrategy{

    @Override
    protected int recoverLife(RegionMes regionMes) {
        if (regionMes.getType().startsWith(RegionEnum.BANK.type()) || regionMes.getType().startsWith(RegionEnum.SEA.type())) {
            return GameCoreHelper.getContext().getRecoverByAbility(AbilityEnum.HILL_CLOSE);
        }
        return 0;
    }

}
