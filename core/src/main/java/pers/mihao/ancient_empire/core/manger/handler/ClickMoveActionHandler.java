package pers.mihao.ancient_empire.core.manger.handler;

import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.core.manger.event.GameEvent;
import pers.mihao.ancient_empire.core.manger.strategy.move_area.MoveAreaStrategy;

import java.util.List;

/**
 * @version 1.0
 * @author mihao
 * @date 2020\10\22 0022 23:32
 */
public class ClickMoveActionHandler extends CommonHandler{


    @Override
    public void handlerCurrentUserGameEvent(GameEvent gameEvent) {
        List<Site> moveArea = MoveAreaStrategy.getInstance().getMoveArea(record(), currUnit());
        showMoveArea(moveArea);
    }
}
