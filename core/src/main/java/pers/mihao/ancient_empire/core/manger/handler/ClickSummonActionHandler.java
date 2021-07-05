package pers.mihao.ancient_empire.core.manger.handler;

import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.core.constans.ExtMes;
import pers.mihao.ancient_empire.core.eums.GameCommendEnum;
import pers.mihao.ancient_empire.core.eums.StatusMachineEnum;
import pers.mihao.ancient_empire.core.manger.event.GameEvent;
import pers.mihao.ancient_empire.core.manger.strategy.attach.AttachStrategy;

import java.util.List;

/**
 * 召唤坟墓事件 产生坟墓消失命令，出现单位命令，更新军队信息命令(单位数量）
 * {@link pers.mihao.ancient_empire.core.eums.GameEventEnum.SUMMON_TOMB}
 * @Author mihao
 * @Date 2020/9/17 16:07
 */
public class ClickSummonActionHandler extends CommonHandler {

    @Override
    public void handlerCurrentUserGameEvent(GameEvent gameEvent) {
        List<Site> attachArea = AttachStrategy.getInstance().getAttachArea(currUnit().getUnitMes(), currSite(), gameMap());
        commandStream()
            .toGameCommand().addCommand(GameCommendEnum.DIS_SHOW_ACTION)
            .toGameCommand().addCommand(GameCommendEnum.SHOW_ATTACH_AREA, ExtMes.ATTACH_AREA, attachArea);
        gameContext.setStatusMachine(StatusMachineEnum.WILL_SUMMON);
        gameContext.setWillAttachArea(attachArea);
    }
}
