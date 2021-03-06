package pers.mihao.ancient_empire.core.manger.handler;

import pers.mihao.ancient_empire.core.eums.GameCommendEnum;
import pers.mihao.ancient_empire.core.eums.StatusMachineEnum;
import pers.mihao.ancient_empire.core.manger.event.GameEvent;

/**
 * 处理点击坟墓事件
 * @version 1.0
 * @author mihao
 * @date 2020\10\22 0022 23:58
 */
public class ClickTombHandler extends CommonHandler{
    @Override
    public void handlerCurrentUserGameEvent(GameEvent gameEvent) {
        if (gameContext.getStatusMachine().equals(StatusMachineEnum.WILL_SUMMON)) {
            // 如果此时状态是准备攻击 判断是否是准备攻击单位
            if (siteInArea(gameEvent.getInitiateSite(), gameContext.getWillAttachArea())) {
                // 点击在攻击范围内的敌方单位 展示攻击确定圈
                commandStream().toGameCommand().addCommand(GameCommendEnum.SHOW_ATTACH_POINT, gameEvent.getInitiateSite());
                gameContext.setBeSummonTomb(gameEvent.getInitiateSite());
            } else {
                // 点击其他区域的单位就返回
                showAction(gameContext.getActions());
                gameContext.setStatusMachine(StatusMachineEnum.MOVE_DONE);
            }
        }else {
            changeCurrRegion(gameEvent.getInitiateSite());
            changeCurrPoint(gameEvent.getInitiateSite());
        }
    }
}
