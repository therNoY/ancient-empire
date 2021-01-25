package pers.mihao.ancient_empire.core.robot.handler;

import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.core.eums.GameEventEnum;
import pers.mihao.ancient_empire.core.eums.SubStatusMachineEnum;
import pers.mihao.ancient_empire.core.manger.GameContext;
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

    Logger log = LoggerFactory.getLogger(AbstractRobotHandler.class);
    /**
     * 攻击范围
     */
    protected final List<Site> attachArea = getUnitAttachArea(moveArea);

    @Override
    public final void setGameContext(GameContext gameContext) {
        this.gameContext = gameContext;
    }

    /**
     * 移动到目标点
     *
     * @param site
     */
    protected void moveToAimPointAndAction(Site site) {
        // 1. 展示移动区域
        handleRobotEvent(GameEventEnum.CLICK_ACTIVE_UNIT, currUnit());

        // 2. 选择离目标最近的点移动
        // 2.1 选择单位可以被够到的任意位置
        List<Site> moveArea = gameContext.getWillMoveArea();
        List<Site> area = getCurrentUnitCanActionArea(site);
        List<Site> canMoveArea = moveArea.stream().filter(s -> area.contains(area)).collect(Collectors.toList());
        if (canMoveArea != null && canMoveArea.size() > 0) {
            moveArea = canMoveArea;
        }
        // 2.2 获取将要移动的目标点
        Site aimSite = moveArea.stream().min((s1, s2) -> {
            if (getSiteLength(s1, site) > getSiteLength(s2, site)) {
                return 1;
            }
            return -1;
        }).get();

        // 2.3 展示移动路线
        log.info("展示移动路线 目标点：{}", site);
        handleRobotEvent(GameEventEnum.CLICK_MOVE_AREA, aimSite);

        // 2.4 移动
        log.info("准备移动单位{} 到目标点：{}", currUnit().getUnitMes(), site);
        robotMoveAndAction();
    }

    protected void robotMoveAndAction() {
        // 1.点击目标区域
        handleRobotEvent(GameEventEnum.CLICK_AIM_POINT, null);

        // 2.点行动按钮
        handleRobotEvent(getActionType(), null);

        if (!gameContext.getSubStatusMachine().equals(SubStatusMachineEnum.INIT)) {
            log.info("进行二次移动 此时选择 最不危险的区域");
            // TODO
            handleRobotEvent(GameEventEnum.CLICK_END_ACTION, null);
        }
    }

    /**
     * 获取移动类型
     * @return
     */
    protected abstract GameEventEnum getActionType();

    /**
     * 处理行动
     *
     * @param intention
     */
    public abstract void handler(ActionIntention intention);





}
