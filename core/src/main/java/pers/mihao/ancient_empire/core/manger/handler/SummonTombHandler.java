package pers.mihao.ancient_empire.core.manger.handler;

import pers.mihao.ancient_empire.core.eums.GameCommendEnum;
import pers.mihao.ancient_empire.core.manger.command.GameCommand;
import pers.mihao.ancient_empire.core.manger.event.GameEvent;

/**
 * 召唤坟墓事件 产生坟墓消失命令，出现单位命令，更新军队信息命令(单位数量）
 * {@link pers.mihao.ancient_empire.core.eums.GameEventEnum.SUMMON_TOMB}
 * @Author mh32736
 * @Date 2020/9/17 16:07
 */
public class SummonTombHandler extends AbstractGameEventHandler{

    @Override
    public void handlerGameEvent(GameEvent gameEvent) {
        // 添加召唤坟墓事件
        GameCommand command = sendToGameCommand();
        command.setGameCommendEnum(GameCommendEnum.TOMB_DISAPPEAR);
        command.setPointSite(gameEvent.getPassiveSite());
        addCommand(command);
    }
}
