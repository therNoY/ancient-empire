package pers.mihao.ancient_empire.core.robot.handler;

import java.util.List;
import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.core.manger.GameContext;
import pers.mihao.ancient_empire.core.robot.ActionIntention;
import pers.mihao.ancient_empire.core.robot.GameAnalysis;

/**
 * @Author mh32736
 * @Date 2020/11/10 21:05
 */
public abstract class AbstractRobotHandler extends GameAnalysis {

    /**
     * 移动范围
     */
    protected final List<Site> moveArea = null;
    /**
     * 攻击范围
     */
    protected final List<Site> attachArea = getUnitAttachArea();

    @Override
    public final void setGameContext(GameContext gameContext) {

    }

    /**
     * 处理行动
     * @param intention
     */
    public abstract void handler(ActionIntention intention);
}
