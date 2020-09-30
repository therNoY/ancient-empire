package pers.mihao.ancient_empire.core.manger.handler;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.mihao.ancient_empire.base.bo.BaseSquare;
import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.util.AppUtil;
import pers.mihao.ancient_empire.core.eums.GameCommendEnum;
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

    /**
     * 处理点击不能行动的单位
     * @param gameEvent
     */
    @Override
    public void handlerGameEvent(GameEvent gameEvent) {
        // 设置当前点
        userRecord().setCurrPoint(gameEvent.getInitiateSite());
        // 设置当前单位
        Unit unit = getUnitBySite(gameEvent.getInitiateSite());
        userRecord().setCurrUnit(unit);
        // 设置当前地形
        BaseSquare square = getRegionBySite(gameEvent.getInitiateSite());
        userRecord().setCurrRegion(square);

        // 设置前端执行指令
        commandStream()
            .toGameCommand().addCommand(GameCommendEnum.CHANGE_CURR_POINT)
            .toGameCommand().addCommand(GameCommendEnum.CHANGE_CURR_UNIT, unit)
            .toGameCommand().addCommand(GameCommendEnum.CHANGE_CURR_REGION, square);

    }
}
