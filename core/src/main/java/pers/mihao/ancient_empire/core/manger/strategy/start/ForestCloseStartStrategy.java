package pers.mihao.ancient_empire.core.manger.strategy.start;

import pers.mihao.ancient_empire.base.entity.RegionMes;
import pers.mihao.ancient_empire.base.enums.AbilityEnum;
import pers.mihao.ancient_empire.base.enums.RegionEnum;
import pers.mihao.ancient_empire.core.util.GameCoreHelper;

/**
 * 森林之子 回合开始回血
 * @Author mihao
 * @Date 2021/2/3 12:49
 */
public class ForestCloseStartStrategy extends StartStrategy{

    @Override
    protected int recoverLife(RegionMes regionMes) {
        if (regionMes.getType().equals(RegionEnum.GROVE.type()) || regionMes.getType().equals(RegionEnum.FOREST.type())) {
           return GameCoreHelper.getContext().getRecoverByAbility(AbilityEnum.FOREST_CLOSE);
        }
        return 0;
    }
}
