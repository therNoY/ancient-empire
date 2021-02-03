package pers.mihao.ancient_empire.core.manger.handler;

import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.core.constans.ExtMes;
import pers.mihao.ancient_empire.core.eums.GameCommendEnum;
import pers.mihao.ancient_empire.core.eums.StatusMachineEnum;
import pers.mihao.ancient_empire.core.manger.event.GameEvent;
import pers.mihao.ancient_empire.core.manger.strategy.attach.AttachStrategy;

import java.util.List;

/**
 * 处理准备攻击事件
 *
 * @version 1.0
 * @author mihao
 * @date 2020\10\6 0006 19:32
 * @see {@link pers.mihao.ancient_empire.core.eums.GameEventEnum.ATTACH_UNIT_START}
 */
public class ClickAttachActionHandler extends CommonHandler {

    /**
     * 点击攻击的图标 需要隐藏攻击图标
     * @param gameEvent
     */
    @Override
    public void handlerGameEvent(GameEvent gameEvent) {
        List<Site> attachArea = AttachStrategy.getInstance().getAttachArea(currUnit().getUnitMes(), currSite(), gameMap());
        commandStream().toGameCommand().addCommand(GameCommendEnum.SHOW_ATTACH_AREA, ExtMes.ATTACH_AREA, attachArea);
        gameContext.setStatusMachine(StatusMachineEnum.WILL_ATTACH);
        gameContext.setWillAttachArea(attachArea);
    }
}
