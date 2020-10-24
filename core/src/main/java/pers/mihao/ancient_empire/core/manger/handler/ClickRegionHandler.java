package pers.mihao.ancient_empire.core.manger.handler;

import pers.mihao.ancient_empire.core.constans.ExtMes;
import pers.mihao.ancient_empire.core.eums.GameCommendEnum;
import pers.mihao.ancient_empire.core.eums.StatusMachineEnum;
import pers.mihao.ancient_empire.core.manger.event.GameEvent;

/**
 * @see {@link pers.mihao.ancient_empire.core.eums.GameEventEnum.CLICK_REGION}
 * @version 1.0
 * @auther mihao
 * @date 2020\10\3 0003 15:36
 */
public class ClickRegionHandler extends CommonHandler {

    @Override
    public void handlerGameEvent(GameEvent gameEvent) {
        // 如果现在状态是二次移动 直接返回false
        if (stateIn(StatusMachineEnum.SECOND_MOVE)) {
            return;
        }

        if (stateIn(StatusMachineEnum.WILL_ATTACH , StatusMachineEnum.WILL_SUMMON)) {
            // 如果此时状态是准备攻击 现在的应该回到之前的攻击选择状态
            commandStream().toGameCommand().addCommand(GameCommendEnum.SHOW_ACTION, ExtMes.ACTIONS, gameContext.getActions());
            gameContext.setStatusMachine(StatusMachineEnum.MOVE_DONE);
            return;
        }

        changeCurrPoint(getSiteByRegionIndex(gameEvent.getRegionIndex()));
        changeCurrRegion(gameEvent.getRegionIndex());

        if (stateIn(StatusMachineEnum.SHOW_MOVE_AREA, StatusMachineEnum.MOVING)) {
            // 如果此时状态机是展示移动区域 就取消展示
            commandStream().toGameCommand().addCommand(GameCommendEnum.DIS_SHOW_MOVE_AREA);
            gameContext.setStatusMachine(StatusMachineEnum.NO_CHOOSE);
        }else if (stateIn(StatusMachineEnum.MOVE_DONE)) {
            commandStream().toGameCommand().addCommand(GameCommendEnum.ROLLBACK_MOVE, gameContext.getStartMoveSite(), getCurrUnitIndex());
        }

        gameContext.setStatusMachine(StatusMachineEnum.NO_CHOOSE);

    }
}