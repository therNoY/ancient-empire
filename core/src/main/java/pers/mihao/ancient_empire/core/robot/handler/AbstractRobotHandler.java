package pers.mihao.ancient_empire.core.robot.handler;

import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.core.eums.ActionEnum;
import pers.mihao.ancient_empire.core.eums.GameCommendEnum;
import pers.mihao.ancient_empire.core.eums.GameEventEnum;
import pers.mihao.ancient_empire.core.eums.SubStatusMachineEnum;
import pers.mihao.ancient_empire.core.manger.GameContext;
import pers.mihao.ancient_empire.core.robot.AbstractRobot;
import pers.mihao.ancient_empire.core.robot.ActionIntention;
import pers.mihao.ancient_empire.core.robot.RobotCommonHandler;

/**
 * 基础机器人行动处理类
 *
 * @Author mh32736
 * @Date 2020/11/10 21:05
 */
public abstract class AbstractRobotHandler extends RobotCommonHandler {

    /**
     * 移动范围
     */
    protected final List<Site> moveArea = null;

    protected AbstractRobot robot;

    Logger log = LoggerFactory.getLogger(AbstractRobotHandler.class);


    @Override
    public final void setGameContext(GameContext gameContext) {
        this.gameContext = gameContext;
    }

    public void setRobot(AbstractRobot robot) {
        this.robot = robot;
    }

    /**
     * 移动到目标点
     *
     * @param site
     */
    protected void moveToAimPointAndAction(ActionIntention intention, Site site) {
        // 1. 选择离目标最近的点移动
        List<Site> moveArea = gameContext.getWillMoveArea().stream()
                .filter(s-> !isHaveFriend(record(), s.getRow(), s.getColumn())).collect(Collectors.toList());
        // 去掉右方所在位置
        List<Site> area = getCanActionArea(site);
        List<Site> canMoveArea = moveArea.stream().filter(area::contains).collect(Collectors.toList());
        if (canMoveArea.size() > 0) {
            moveArea = canMoveArea;
        }
        // 2 获取将要移动的目标点
        Site aimSite = moveArea.stream().min((s1, s2) -> {
            if (getSiteLength(s1, site) > getSiteLength(s2, site)) {
                return 1;
            }
            return -1;
        }).get();
        assert moveArea.contains(aimSite);
        // 2.1 展示移动路线
        log.info("展示移动路线 目标点：{}", site);
        handleRobotEvent(GameEventEnum.CLICK_MOVE_AREA, aimSite);

        // 2.2 移动
        log.info("准备移动单位{} 到目标点：{}", currUnit().getUnitMes(), site);
        robotMoveAndAction(intention);
    }

    /**
     * 获取行动可以作用到的区域 不同的事件不同
     * @param site
     */
    protected abstract List<Site> getCanActionArea(Site site);

    protected void robotMoveAndAction(ActionIntention intention) {
        // 1.点击目标区域
        handleRobotEvent(GameEventEnum.CLICK_AIM_POINT);

        // 2.点行动按钮
        sendGameCommand(GameCommendEnum.DIS_SHOW_ACTION);
        if (gameContext.getActions().contains(getActionType().type())) {
            doClickActionEvent(intention);
            if (!gameContext.getSubStatusMachine().equals(SubStatusMachineEnum.INIT)) {
                log.info("进行二次移动 此时选择 最不危险的区域");
                // TODO
                handleRobotEvent(GameEventEnum.CLICK_END_ACTION);
            }
        }else {
            handleRobotEvent(GameEventEnum.CLICK_END_ACTION);
        }
    }

    /**
     * 点击行动事件
     */
    protected abstract void doClickActionEvent(ActionIntention intention);

    /**
     * 获取移动类型
     * @return
     */
    protected abstract ActionEnum getActionType();


    /**
     * 处理机器人行动
     *
     * @param intention
     */
    public abstract void handler(ActionIntention intention);

}
