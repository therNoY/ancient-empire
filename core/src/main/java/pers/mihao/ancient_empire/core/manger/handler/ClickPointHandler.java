package pers.mihao.ancient_empire.core.manger.handler;

import pers.mihao.ancient_empire.core.constans.ExtMes;
import pers.mihao.ancient_empire.core.dto.ArmyUnitIndexDTO;
import pers.mihao.ancient_empire.core.eums.GameCommendEnum;
import pers.mihao.ancient_empire.core.eums.StatusMachineEnum;
import pers.mihao.ancient_empire.core.manger.event.GameEvent;
import pers.mihao.ancient_empire.core.manger.strategy.action.ActionStrategy;

import java.util.Set;

/**
 * @see {@link pers.mihao.ancient_empire.core.eums.GameEventEnum.CLICK_POINT}
 * @version 1.0
 * @author mihao
 * @date 2020\10\17 0017 16:10
 */
public class ClickPointHandler extends CommonHandler{

    @Override
    public void handlerGameEvent(GameEvent gameEvent) {
        // 点前是准备移动阶段 目标还在当前点 不移动
        if (stateIn(StatusMachineEnum.SHOW_MOVE_AREA)) {
            // 原地行动
            Set<String> actions = ActionStrategy.getInstance().getActionList(getCurrUnitAttachArea(), record(), record().getCurrPoint());
            gameContext.setActions(actions);
            gameContext.setStartMoveSite(currSite());
            gameContext.setReadyMoveLine(null);
            gameContext.setReadyMoveSite(currSite());
            commandStream()
                    .toGameCommand().addCommand(GameCommendEnum.DIS_SHOW_MOVE_AREA)
                    .toGameCommand().showAction(gameContext.getActions());
            gameContext.setStatusMachine(StatusMachineEnum.MOVE_DONE);
        }else if (stateIn(StatusMachineEnum.SECOND_MOVE)) {
            ArmyUnitIndexDTO indexDTO = currUnitArmyIndex();
            // 隐藏单位移动区域
            commandStream().toGameCommand().addCommand(GameCommendEnum.DIS_SHOW_MOVE_AREA);
            sendEndUnitCommend(currUnit(), indexDTO);
        }
    }
}
