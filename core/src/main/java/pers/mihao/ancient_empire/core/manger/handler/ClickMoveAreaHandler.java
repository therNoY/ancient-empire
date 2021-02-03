package pers.mihao.ancient_empire.core.manger.handler;

import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.core.constans.ExtMes;
import pers.mihao.ancient_empire.core.dto.PathPosition;
import pers.mihao.ancient_empire.core.eums.GameCommendEnum;
import pers.mihao.ancient_empire.core.eums.StatusMachineEnum;
import pers.mihao.ancient_empire.core.manger.event.GameEvent;
import pers.mihao.ancient_empire.core.manger.strategy.move_path.MovePathStrategy;

import java.util.List;

/**
 * 点击启动区域
 * @see {@link pers.mihao.ancient_empire.core.eums.GameEventEnum.CLICK_MOVE_AREA}
 * @version 1.0
 * @author mihao
 * @date 2020\10\4 0004 15:37
 */
public class ClickMoveAreaHandler extends CommonHandler{


    @Override
    public void handlerGameEvent(GameEvent gameEvent) {

        changeCurrPoint(gameEvent.getInitiateSite());

        changeCurrRegion(gameEvent.getInitiateSite());

        showMoveLine(gameEvent.getInitiateSite());
    }


}
