package pers.mihao.ancient_empire.core.robot.handler;

import pers.mihao.ancient_empire.common.util.Pair;
import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.bo.UnitInfo;
import pers.mihao.ancient_empire.common.util.BeanUtil;
import pers.mihao.ancient_empire.core.eums.ActionEnum;
import pers.mihao.ancient_empire.core.eums.GameEventEnum;
import pers.mihao.ancient_empire.core.robot.ActionIntention;
import pers.mihao.ancient_empire.core.robot.RobotActiveEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * 处理攻击类行动
 *
 * @Author mihao
 * @Date 2020/11/10 21:03
 * @see {@link RobotActiveEnum.ATTACH}
 */
public class AttachRobotHandler extends AbstractRobotHandler {


    @Override
    protected List<Site> getCanActionArea(Site site) {
        return getCurrentUnitCanActionArea(site);
    }

    @Override
    protected ActionEnum getActionType() {
        return ActionEnum.ATTACK;
    }

    @Override
    protected void doClickActionEvent(ActionIntention intention) {
        handleRobotEvent(GameEventEnum.CLICK_ATTACH_ACTION);
        UnitInfo unitInfo = intention.getAimUnit();
        List<Site> sites = gameContext.getWillAttachArea();
        Site aimSite = (Site) BeanUtil.copyValueToParent(unitInfo, Site.class);
        if (sites.contains(aimSite)) {
            handleRobotEvent(GameEventEnum.CLICK_UN_ACTIVE_UNIT, aimSite);
            handleRobotEvent(GameEventEnum.CLICK_CHOOSE_POINT, null, aimSite);
        } else {
            log.info("可以攻击 其他单位的点");
            Site site;
            Pair<Integer, Unit> pair;
            List<Unit> otherCanAttachUnit = new ArrayList<>();
            Unit chooseUnit = null;
            for (int i = 0; i < sites.size(); i++) {
                site = sites.get(i);
                pair = getEnemyUnitFromMapBySite(site);
                if (pair != null) {
                    otherCanAttachUnit.add(pair.getValue());
                }
            }
            if (otherCanAttachUnit.size() > 0) {
                chooseUnit = robot.getOtherCanAttachUnit(otherCanAttachUnit);
            }
            if (chooseUnit != null) {
                handleRobotEvent(GameEventEnum.CLICK_UN_ACTIVE_UNIT, chooseUnit);
                handleRobotEvent(GameEventEnum.CLICK_CHOOSE_POINT, chooseUnit, chooseUnit);
            }
        }

    }

}
