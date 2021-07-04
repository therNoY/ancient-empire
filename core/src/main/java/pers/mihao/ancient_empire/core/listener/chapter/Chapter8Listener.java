package pers.mihao.ancient_empire.core.listener.chapter;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import pers.mihao.ancient_empire.common.util.Pair;
import pers.mihao.ancient_empire.base.bo.Army;
import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.bo.UnitInfo;
import pers.mihao.ancient_empire.base.util.AppUtil;
import pers.mihao.ancient_empire.core.eums.DialogEnum;
import pers.mihao.ancient_empire.core.robot.ActionIntention;
import pers.mihao.ancient_empire.core.robot.RobotActiveEnum;

/**
 * 第一章监听处理类
 *
 * @Author mihao
 * @Date 2021/4/1 9:20
 */
public class Chapter8Listener extends AbstractChapterListener {

    /**
     * boos的位置
     */
    private static final Site BOOS_SITE = new Site(3, 8);


    @Override
    protected void initConditions() {

    }

    @Override
    protected void onChapterGameOver() {

    }

    @Override
    public void onChapterGameStart() {
        addDialogAndWait(DialogEnum.LOADER_BLUE, "CAMPAIGN_AEII_STAGE_8_MESSAGE_1");
        addDialogAndWait(DialogEnum.LOADER_RED, "CAMPAIGN_AEII_STAGE_8_MESSAGE_2");
        Unit unit1 = moveUnit(new Site(3, 7), new Site(3, 8));
        removeUnit(getArmyUnitIndexByUnitId(unit1.getId()));
        Unit unit2 = moveUnit(new Site(3, 9), new Site(3, 8));
        removeUnit(getArmyUnitIndexByUnitId(unit2.getId()));
        Unit unit3 = moveUnit(new Site(4, 8), new Site(3, 8));
        removeUnit(getArmyUnitIndexByUnitId(unit3.getId()));
        sendCommandNow();
        waitExecutionOk(15000);
        addDialogAndWait(DialogEnum.LOADER_RED, "CAMPAIGN_AEII_STAGE_8_MESSAGE_3");
        moveUnit(new Site(5, 8), new Site(3, 8));
        sendCommandNow();
        waitExecutionOk(3000);
        addDialogAndWait(DialogEnum.LOADER_RED, "CAMPAIGN_AEII_STAGE_8_MESSAGE_3_1");
        addDialogAndWait(DialogEnum.LOADER_BLUE, "CAMPAIGN_AEII_STAGE_8_MESSAGE_4");
        addDialogAndWait(DialogEnum.LOADER_RED, "CAMPAIGN_AEII_STAGE_8_MESSAGE_5");

        Pair<Integer, Unit> beAttach = getUnitFromMapBySite(new Site(16, 10));
        showHeavenFury(beAttach.getValue(), 100);
        sendCommandNow();
        waitExecutionOk(9000);

        addDialogAndWait(DialogEnum.LOADER_BLUE, "CAMPAIGN_AEII_STAGE_8_MESSAGE_6");
        addDialogAndWait(DialogEnum.LOADER_BLUE, "CAMPAIGN_AEII_STAGE_8_MESSAGE_7");
        addDialogAndWait(DialogEnum.LOADER_BLUE, "CAMPAIGN_AEII_STAGE_8_MESSAGE_8");
        addDialogAndWait(DialogEnum.FRIEND_UNIT1, "CAMPAIGN_AEII_STAGE_8_MESSAGE_9");
        addDialogAndWait(DialogEnum.WIN_CONDITION, "CAMPAIGN_AEII_STAGE_8_OBJECTIVE");
    }


    @Override
    public void onChapterGameWin() {
        addDialogAndWait(DialogEnum.FRIEND_UNIT1, "CAMPAIGN_AEII_STAGE_8_MESSAGE_10");
        addDialogAndWait(DialogEnum.FRIEND_UNIT1, "CAMPAIGN_AEII_STAGE_8_MESSAGE_11");
        addDialogAndWait(DialogEnum.FRIEND_UNIT1, "CAMPAIGN_AEII_STAGE_8_MESSAGE_12");
        addDialogAndWait(DialogEnum.FRIEND_UNIT1, "CAMPAIGN_AEII_STAGE_8_MESSAGE_13");
        addDialogAndWait(DialogEnum.FRIEND_UNIT1, "CAMPAIGN_AEII_STAGE_8_MESSAGE_14");
        addDialogAndWait(DialogEnum.FRIEND_UNIT1, "CAMPAIGN_AEII_STAGE_8_MESSAGE_14");
        addDialogAndWait(DialogEnum.FRIEND_UNIT1, "CAMPAIGN_AEII_STAGE_8_MESSAGE_15");
        addDialogAndWait(DialogEnum.FRIEND_UNIT1, "CAMPAIGN_AEII_STAGE_8_MESSAGE_16");
    }


    @Override
    public void beforeRoundStart(Army currArmy) {
        if (currArmy.getCamp() == ENEMY_CAMP) {
            Unit unit = selectHeavenFuryAim();
            if (unit != null) {
                showHeavenFury(unit, 99);
                sendCommandNow();
                waitExecutionOk(10000);
            }
        }
    }

    @Override
    public ActionIntention chooseAction(UnitInfo unitInfo, List<ActionIntention> actionList) {
        if (AppUtil.siteEquals(BOOS_SITE, unitInfo)) {
            // boos直接结束
            return new ActionIntention(RobotActiveEnum.END, BOOS_SITE);
        }
        return null;
    }

    /**
     * 根据单位的价格排序
     *
     * @return
     */
    private Unit selectHeavenFuryAim() {

        Army friendArmy = record().getArmyList()
            .stream().filter(army -> army.getCamp().equals(FRIEND_CAMP))
            .findFirst().orElse(null);

        if (friendArmy == null) {
            return null;
        }

        Optional<UnitInfo> unitInfo = friendArmy.getUnits().stream()
            .map(this::getUnitInfoByUnit)
            .sorted(Comparator.comparingInt(u -> u.getLevel() * -1))
            .sorted(Comparator.comparingInt(u -> u.getUnitMes().getPrice() * -1))
            .max(Comparator.comparingInt(Unit::getLife));

        return unitInfo.orElse(null);
    }
}
