package pers.mihao.ancient_empire.core.robot.handler;

import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.core.eums.ActionEnum;
import pers.mihao.ancient_empire.core.eums.GameEventEnum;
import pers.mihao.ancient_empire.core.robot.AbstractRobot;
import pers.mihao.ancient_empire.core.robot.ActionIntention;
import pers.mihao.ancient_empire.core.robot.RobotActiveEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * 处理占领类行动
 * @Author mihao
 * @Date 2020/11/10 21:03
 * @see {@link RobotActiveEnum.OCCUPIED}
 */
public class OccupiedRobotHandler extends AbstractRobotHandler{


    @Override
    protected ActionEnum getActionType() {
        return ActionEnum.OCCUPIED;
    }

    @Override
    protected List<Site> getCanActionArea(Site site) {
        List<Site> area = new ArrayList<>();
        area.add(site);
        return area;
    }

    @Override
    protected void doClickActionEvent(ActionIntention intention) {
        handleRobotEvent(GameEventEnum.CLICK_OCCUPIED_ACTION);
    }
}
