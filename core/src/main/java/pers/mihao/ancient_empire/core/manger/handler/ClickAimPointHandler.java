package pers.mihao.ancient_empire.core.manger.handler;

import com.alibaba.fastjson.JSONObject;
import pers.mihao.ancient_empire.core.constans.ExtMes;
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
 * @auther mihao
 * @date 2020\10\4 0004 17:32
 */
public class ClickAimPointHandler extends CommonHandler{

    @Override
    public void handlerGameEvent(GameEvent gameEvent) {

        if (subStateIn(SubStatusMachineEnum.SECOND_MOVE)) {
            // 二次移动直接结束单位的移动
            ArmyUnitIndexDTO armyUnitIndexDTO = currUnitArmyIndex();
            JSONObject extMes = new JSONObject();
            extMes.put(ExtMes.MOVE_LINE, gameContext.getReadyMoveLine());
            extMes.put(ExtMes.ACTIONS, new ArrayList<>());
            commandStream().toGameCommand().addCommand(GameCommendEnum.DIS_SHOW_MOVE_AREA)
                    .toGameCommand().addOrderCommand(GameCommendEnum.MOVE_UNIT, extMes, armyUnitIndexDTO.getUnitIndex());
            gameContext.setStatusMachine(StatusMachineEnum.INIT);
            sendEndUnitCommend(currUnit(), armyUnitIndexDTO);
        }else {
            Set<String> actions = ActionStrategy.getInstance().getActionList(getCurrUnitAttachArea(), record(), record().getCurrPoint());

            // 不展示移动范围
            JSONObject extMes = new JSONObject();
            extMes.put(ExtMes.MOVE_LINE, gameContext.getReadyMoveLine());
            extMes.put(ExtMes.ACTIONS, actions);
            commandStream().toGameCommand().addCommand(GameCommendEnum.DIS_SHOW_MOVE_AREA)
                    .toGameCommand().addCommand(GameCommendEnum.MOVE_UNIT, extMes, getCurrUnitIndex());
            gameContext.setStartMoveSite(currUnit());
            gameContext.setStatusMachine(StatusMachineEnum.MOVE_DONE);
            gameContext.setActions(actions);
        }
    }
}
