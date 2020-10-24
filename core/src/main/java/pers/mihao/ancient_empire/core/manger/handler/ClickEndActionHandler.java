package pers.mihao.ancient_empire.core.manger.handler;

import pers.mihao.ancient_empire.core.eums.GameCommendEnum;
import pers.mihao.ancient_empire.core.eums.StatusMachineEnum;
import pers.mihao.ancient_empire.core.manger.event.GameEvent;

/**
 * 单位结束处理类
 * @version 1.0
 * @auther mihao
 * @date 2020\10\18 0018 13:34
 */
public class ClickEndActionHandler extends CommonHandler{

    @Override
    public void handlerGameEvent(GameEvent gameEvent) {
        // 单位结束移动
        sendEndUnitCommend(currUnit(), currUnitArmyIndex());
        // 考虑是否结束回合
    }
}