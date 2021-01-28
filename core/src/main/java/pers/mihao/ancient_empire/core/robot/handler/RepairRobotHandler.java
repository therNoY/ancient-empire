package pers.mihao.ancient_empire.core.robot.handler;

import pers.mihao.ancient_empire.core.eums.GameEventEnum;
import pers.mihao.ancient_empire.core.robot.ActionIntention;
import pers.mihao.ancient_empire.core.robot.RobotActiveEnum;

/**
 * 处理修复类行动
 * @Author mh32736
 * @Date 2020/11/10 21:03
 * @see {@link RobotActiveEnum.REPAIR}
 */
public class RepairRobotHandler extends AbstractRobotHandler{

    @Override
    protected GameEventEnum getActionType() {
        return GameEventEnum.CLICK_REPAIR_ACTION;
    }

    @Override
    public void handler(ActionIntention intention) {
        moveToAimPointAndAction(intention.getSite());
    }
}
