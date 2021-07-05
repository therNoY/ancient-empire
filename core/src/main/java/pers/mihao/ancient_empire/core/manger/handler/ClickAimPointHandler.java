package pers.mihao.ancient_empire.core.manger.handler;

import pers.mihao.ancient_empire.core.dto.ArmyUnitIndexDTO;
import pers.mihao.ancient_empire.core.eums.GameCommendEnum;
import pers.mihao.ancient_empire.core.eums.StatusMachineEnum;
import pers.mihao.ancient_empire.core.eums.SubStatusMachineEnum;
import pers.mihao.ancient_empire.core.manger.event.GameEvent;
import pers.mihao.ancient_empire.core.manger.strategy.action.ActionStrategy;

import java.util.ArrayList;
import java.util.Set;

/**
 * 点击目标的移动区域 移动单位
 * @see {@link pers.mihao.ancient_empire.core.eums.GameEventEnum.CLICK_AIM_POINT}
 * @version 1.0
 * @author mihao
 * @date 2020\10\4 0004 17:32
 */
public class ClickAimPointHandler extends CommonHandler{

    @Override
    public void handlerCurrentUserGameEvent(GameEvent gameEvent) {

        if (subStateIn(SubStatusMachineEnum.SECOND_MOVE)) {
            // 二次移动直接结束单位的移动
            ArmyUnitIndexDTO armyUnitIndexDTO = currUnitArmyIndex();

            commandStream().toGameCommand().addCommand(GameCommendEnum.DIS_SHOW_MOVE_AREA);
            moveUnit(armyUnitIndexDTO, gameContext.getReadyMoveLine(), new ArrayList<>());
            gameContext.setStatusMachine(StatusMachineEnum.INIT);
            sendEndUnitCommend(currUnit(), armyUnitIndexDTO);
        }else {
            Set<String> actions = ActionStrategy.getInstance().getActionList(getCurrUnitAttachArea(), record(), record().getCurrPoint());

            // 不展示移动范围
            commandStream().toGameCommand().addCommand(GameCommendEnum.DIS_SHOW_MOVE_AREA);
            moveUnit(currUnitArmyIndex(), gameContext.getReadyMoveLine(), actions);
            gameContext.setStartMoveSite(currUnit());
            gameContext.setStatusMachine(StatusMachineEnum.MOVE_DONE);
            gameContext.setActions(actions);
        }
    }


}
