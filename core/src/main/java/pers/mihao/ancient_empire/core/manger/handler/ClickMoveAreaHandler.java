package pers.mihao.ancient_empire.core.manger.handler;

import pers.mihao.ancient_empire.core.manger.event.GameEvent;

/**
 * 点击启动区域
 * @see {@link pers.mihao.ancient_empire.core.eums.GameEventEnum.CLICK_MOVE_AREA}
 * @version 1.0
 * @author mihao
 * @date 2020\10\4 0004 15:37
 */
public class ClickMoveAreaHandler extends CommonHandler{


    @Override
    public void handlerCurrentUserGameEvent(GameEvent gameEvent) {

        changeCurrPoint(gameEvent.getInitiateSite());

        changeCurrRegion(gameEvent.getInitiateSite());

        showMoveLine(gameEvent.getInitiateSite());
    }


}
