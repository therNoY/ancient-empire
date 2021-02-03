package pers.mihao.ancient_empire.core.manger.handler;

import pers.mihao.ancient_empire.base.bo.RegionInfo;
import pers.mihao.ancient_empire.base.enums.RegionEnum;
import pers.mihao.ancient_empire.core.constans.ExtMes;
import pers.mihao.ancient_empire.core.eums.GameCommendEnum;
import pers.mihao.ancient_empire.core.eums.StatusMachineEnum;
import pers.mihao.ancient_empire.core.eums.SubStatusMachineEnum;
import pers.mihao.ancient_empire.core.manger.event.GameEvent;

/**
 * @version 1.0
 * @author mihao
 * @date 2020\10\3 0003 15:36
 * @see {@link pers.mihao.ancient_empire.core.eums.GameEventEnum.CLICK_REGION}
 */
public class ClickRegionHandler extends CommonHandler {

    @Override
    public void handlerGameEvent(GameEvent gameEvent) {
        // 如果现在状态是二次移动 直接返回false
        if (stateIn(StatusMachineEnum.SECOND_MOVE, StatusMachineEnum.MAST_MOVE)) {
            return;
        }

        if (stateIn(StatusMachineEnum.WILL_ATTACH, StatusMachineEnum.WILL_SUMMON, StatusMachineEnum.WILL_ATTACH_REGION)) {
            // 如果此时状态是准备攻击 现在的应该回到之前的攻击选择状态
            commandStream().toGameCommand().showAction(gameContext.getActions());
            gameContext.setStatusMachine(StatusMachineEnum.MOVE_DONE);
            return;
        }

        changeCurrPoint(getSiteByRegionIndex(gameEvent.getRegionIndex()));
        RegionInfo currRegion = changeCurrRegion(gameEvent.getRegionIndex());

        if (subStateIn(SubStatusMachineEnum.MAST_MOVE, SubStatusMachineEnum.SECOND_MOVE)) {
            // 如果当前子状态是 必须移动 那么就返回 并设置必须移动
            commandStream()
                    .toGameCommand().addCommand(GameCommendEnum.ROLLBACK_MOVE, gameContext.getStartMoveSite(), getCurrUnitIndex());
            showMoveArea(gameContext.getWillMoveArea());
            gameContext.setStatusMachine(StatusMachineEnum.MAST_MOVE);
            return;
        }

        if (stateIn(StatusMachineEnum.SHOW_MOVE_AREA, StatusMachineEnum.SHOW_MOVE_LINE)) {
            // 如果此时状态机是展示移动区域 就取消展示
            commandStream().toGameCommand().addCommand(GameCommendEnum.DIS_SHOW_MOVE_AREA);
            gameContext.setStatusMachine(StatusMachineEnum.INIT);
        } else if (stateIn(StatusMachineEnum.MOVE_DONE)) {
            commandStream().toGameCommand().addCommand(GameCommendEnum.ROLLBACK_MOVE, gameContext.getStartMoveSite(), getCurrUnitIndex());
        } else if (currRegion.getType().equals(RegionEnum.CASTLE.type()) && record().getCurrColor().equals(currRegion.getColor())) {
            commandStream().toUserCommand().addCommand(GameCommendEnum.SHOW_BUY_UNIT);
        }

        gameContext.setStatusMachine(StatusMachineEnum.INIT);

    }
}
