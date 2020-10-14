package pers.mihao.ancient_empire.core.manger.handler;

import pers.mihao.ancient_empire.core.eums.GameCommendEnum;
import pers.mihao.ancient_empire.core.eums.SendTypeEnum;
import pers.mihao.ancient_empire.core.manger.command.GameCommand;
import pers.mihao.ancient_empire.core.manger.event.GameEvent;

/**
 * 召唤坟墓事件 产生坟墓消失命令，出现单位命令，更新军队信息命令(单位数量）
 * {@link pers.mihao.ancient_empire.core.eums.GameEventEnum.SUMMON_TOMB}
 * @Author mh32736
 * @Date 2020/9/17 16:07
 */
public class SummonTombHandler extends BaseHandler {

    @Override
    public void handlerGameEvent(GameEvent gameEvent) {
        // 添加召唤坟墓事件
        GameCommand command = new GameCommand();
        command.setGameCommendEnum(GameCommendEnum.TOMB_DISAPPEAR);
        command.setSendTypeEnum(SendTypeEnum.SEND_TO_GAME);
        command.setAimSite(gameEvent.getAimSite());
        addGameCommand(command);

        GameCommand command2 = new GameCommand();
        command2.setGameCommendEnum(GameCommendEnum.TOMB_DISAPPEAR);
        command2.setSendTypeEnum(SendTypeEnum.SEND_TO_GAME);
        command2.setAimSite(gameEvent.getAimSite());
        addGameCommand(command2);

        // 使用流式 连续发送多个命令
        commandStream()
            .toGameCommand().addCommand(GameCommendEnum.TOMB_DISAPPEAR, gameEvent.getAimSite())
            .toGameCommand().addCommand(GameCommendEnum.TOMB_DISAPPEAR, gameEvent.getAimSite());
    }
}
