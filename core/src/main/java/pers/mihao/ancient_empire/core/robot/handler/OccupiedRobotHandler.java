package pers.mihao.ancient_empire.core.robot.handler;

import pers.mihao.ancient_empire.core.eums.GameEventEnum;
import pers.mihao.ancient_empire.core.robot.ActionIntention;
import pers.mihao.ancient_empire.core.robot.RobotActiveEnum;

/**
 * 处理占领类行动
 * @Author mh32736
 * @Date 2020/11/10 21:03
 * @see {@link RobotActiveEnum.OCCUPIED}
 */
public class OccupiedRobotHandler extends AbstractRobotHandler{

    @Override
    public void handler(ActionIntention intention) {
        moveToAimPointAndAction(intention.getSite());
    }

    @Override
    protected GameEventEnum getActionType() {
        return GameEventEnum.CLICK_OCCUPIED_ACTION;
    }
}
