package pers.mihao.ancient_empire.core.robot.handler;

import javafx.util.Pair;
import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.base.bo.Tomb;
import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.bo.UnitInfo;
import pers.mihao.ancient_empire.common.util.BeanUtil;
import pers.mihao.ancient_empire.core.eums.ActionEnum;
import pers.mihao.ancient_empire.core.eums.GameEventEnum;
import pers.mihao.ancient_empire.core.robot.AbstractRobot;
import pers.mihao.ancient_empire.core.robot.ActionIntention;
import pers.mihao.ancient_empire.core.robot.RobotActiveEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * 处理召唤类行动
 *
 * @Author mh32736
 * @Date 2020/11/10 21:03
 * @see {@link RobotActiveEnum.SUMMON}
 */
public class SummonRobotHandler extends AbstractRobotHandler {

    @Override
    protected ActionEnum getActionType() {
        return ActionEnum.SUMMON;
    }



    @Override
    protected List<Site> getCanActionArea(Site site) {
        return getCurrentUnitCanActionArea(site);
    }

    @Override
    protected void doClickActionEvent(ActionIntention intention) {
        handleRobotEvent(GameEventEnum.CLICK_SUMMON_ACTION);
        Site clickSite = intention.getSite();
        List<Site> sites = gameContext.getWillAttachArea();
        Site aimSite = (Site) BeanUtil.copyValueToParent(clickSite, Site.class);
        if (sites.contains(aimSite)) {
            handleRobotEvent(GameEventEnum.CLICK_TOMB, aimSite);
            handleRobotEvent(GameEventEnum.CLICK_CHOOSE_POINT, null, aimSite);
        } else {
            log.info("可以召唤 其他单位的点");
            Site site;
            List<Tomb> otherCanSummonTomb = new ArrayList<>();
            Tomb chooseTomb = null;
            Tomb tomb;
            for (int i = 0; i < sites.size(); i++) {
                site = sites.get(i);
                tomb = getTombMapBySite(site);
                if (tomb != null) {
                    otherCanSummonTomb.add(tomb);
                }
            }
            if (otherCanSummonTomb.size() > 0) {
                chooseTomb = robot.getOtherCanSummonTomb(otherCanSummonTomb);
            }
            if (chooseTomb != null) {
                handleRobotEvent(GameEventEnum.CLICK_TOMB, chooseTomb);
                handleRobotEvent(GameEventEnum.CLICK_CHOOSE_POINT, null, chooseTomb);
            }
        }
    }
}
