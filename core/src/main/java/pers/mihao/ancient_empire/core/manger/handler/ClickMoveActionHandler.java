package pers.mihao.ancient_empire.core.manger.handler;

import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.core.constans.ExtMes;
import pers.mihao.ancient_empire.core.eums.GameCommendEnum;
import pers.mihao.ancient_empire.core.eums.StatusMachineEnum;
import pers.mihao.ancient_empire.core.manger.GameContext;
import pers.mihao.ancient_empire.core.manger.event.GameEvent;
import pers.mihao.ancient_empire.core.manger.strategy.move_area.MoveAreaStrategy;

import java.util.List;

/**
 * @version 1.0
 * @auther mihao
 * @date 2020\10\22 0022 23:32
 */
public class ClickMoveActionHandler extends CommonHandler{


    @Override
    public void handlerGameEvent(GameEvent gameEvent) {
        List<Site> moveArea = MoveAreaStrategy.getInstance().getMoveArea(record(), currUnit());
        gameContext.setStatusMachine(StatusMachineEnum.SHOW_MOVE_AREA);
        gameContext.setWillMoveArea(moveArea);
        commandStream().toGameCommand().addCommand(GameCommendEnum.SHOW_MOVE_AREA, ExtMes.MOVE_AREA, moveArea);
    }
}
