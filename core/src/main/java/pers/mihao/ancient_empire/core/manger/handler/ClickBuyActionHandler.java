package pers.mihao.ancient_empire.core.manger.handler;

import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.bo.UnitInfo;
import pers.mihao.ancient_empire.base.util.factory.UnitFactory;
import pers.mihao.ancient_empire.common.util.BeanUtil;
import pers.mihao.ancient_empire.core.eums.StatusMachineEnum;
import pers.mihao.ancient_empire.core.eums.SubStatusMachineEnum;
import pers.mihao.ancient_empire.core.manger.event.GameEvent;
import pers.mihao.ancient_empire.core.manger.strategy.move_area.MoveAreaStrategy;

import java.util.List;

/**
 * 购买单位处理类
 * @Author mh32736
 * @Date 2020/9/17 16:04
 */
public class ClickBuyActionHandler extends CommonHandler {

    @Override
    public void handlerGameEvent(GameEvent gameEvent) {

        Unit newUnit = UnitFactory.createUnit(gameEvent.getUnitId(), currSite());

        commandStream().toGameCommand().addUnit(newUnit, record().getCurrArmyIndex());

        // 判断当前位置
        if(currSite().equals(currUnit())) {
            // 领主购买
            UnitInfo newUnitInfo = unitMesService.getUnitInfo(String.valueOf(newUnit.getTypeId()), newUnit.getLevel());
            BeanUtil.copyValue(newUnit, newUnitInfo);
            List<Site> moveArea = MoveAreaStrategy.getInstance().getMoveArea(record(), newUnitInfo);
            showMoveArea(moveArea);
            changeCurrUnit(newUnitInfo);
            gameContext.setStatusMachine(StatusMachineEnum.MAST_MOVE);
            gameContext.setSubStatusMachine(SubStatusMachineEnum.MAST_MOVE);
        }

    }
}
