package pers.mihao.ancient_empire.core.manger.handler;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.mihao.ancient_empire.core.manger.event.GameEvent;

/**
 * 处理点击不能行动的单位
 * @see {@link pers.mihao.ancient_empire.core.eums.GameEventEnum.CLICK_ACTIVE_UNIT}
 * @version 1.0
 * @auther mihao
 * @date 2020\9\29 0029 22:36
 */
public class ClickUnActiveUnitHandler extends AbstractGameEventHandler {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void handlerGameEvent(GameEvent gameEvent) {

    }
}
