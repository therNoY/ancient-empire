package pers.mihao.ancient_empire.core.robot.handler;

import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.core.eums.ActionEnum;
import pers.mihao.ancient_empire.core.eums.GameEventEnum;
import pers.mihao.ancient_empire.core.robot.AbstractRobot;
import pers.mihao.ancient_empire.core.robot.ActionIntention;
import pers.mihao.ancient_empire.core.robot.RobotActiveEnum;

import java.awt.*;
import java.util.List;

/**
 * 处理治疗类行动
 * @Author mh32736
 * @Date 2020/11/10 21:03
 * @see {@link RobotActiveEnum.HEAL}
 */
public class HealRobotHandler extends AbstractRobotHandler{

    @Override
    public void handler(ActionIntention intention) {
        moveToAimPointAndAction(intention, intention.getSite());
    }

    @Override
    protected ActionEnum getActionType() {
        return ActionEnum.HEAL;
    }

    @Override
    protected List<Site> getCanActionArea(Site site) {
        return getCurrentUnitCanActionArea(site);
    }

    @Override
    protected void doClickActionEvent(ActionIntention intention) {
        handleRobotEvent(GameEventEnum.CLICK_HEAL_ACTION);
    }
}
