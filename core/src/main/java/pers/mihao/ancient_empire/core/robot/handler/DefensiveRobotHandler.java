package pers.mihao.ancient_empire.core.robot.handler;

import pers.mihao.ancient_empire.core.eums.GameEventEnum;
import pers.mihao.ancient_empire.core.robot.ActionIntention;
import pers.mihao.ancient_empire.core.robot.RobotActiveEnum;

/**
 * 处理防御类行动
 * @Author mh32736
 * @Date 2020/11/10 21:03
 * @see {@link RobotActiveEnum.DEFENSIVE}
 */
public class DefensiveRobotHandler extends AbstractRobotHandler{

    @Override
    protected GameEventEnum getActionType() {
        return null;
    }

    @Override
    public void handler(ActionIntention intention) {
    }
}
