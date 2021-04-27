package pers.mihao.ancient_empire.core.listener.chapter;

import pers.mihao.ancient_empire.base.bo.Region;
import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.bo.UnitInfo;
import pers.mihao.ancient_empire.common.vo.AncientEmpireException;
import pers.mihao.ancient_empire.core.eums.DialogEnum;
import pers.mihao.ancient_empire.core.listener.chapter.enums.TriggerTypeEnum;

/**
 * 第七章监听 比较特殊 自己监听
 *
 * @Author mh32736
 * @Date 2021/4/1 9:20
 */
public class Chapter7Listener extends AbstractChapterListener {


    @Override
    protected void initConditions() {
        TriggerCondition condition1 = new TriggerCondition(TriggerTypeEnum.END_ROUND);
        condition1.setRound(2);

        TriggerCondition condition2 = new TriggerCondition(TriggerTypeEnum.OUR_AREA);
        condition2.setMinSite(new Site(1, 16));
        condition2.setMaxSite(new Site(11, 12));
        triggerConditions = new TriggerCondition[]{condition1, condition2};
    }

    @Override
    public void onOccupied(UnitInfo currUnit, Region region) {
        triggerStage2();
    }


    @Override
    protected void onChapterGameOver() {

    }

    @Override
    public void onChapterGameStart() {
        addDialogAndWait(DialogEnum.FRIEND_UNIT2, "CAMPAIGN_AEII_STAGE_7_MESSAGE_1");
        addDialogAndWait(DialogEnum.LOADER_BLUE, "CAMPAIGN_AEII_STAGE_7_MESSAGE_2");
        addDialogAndWait(DialogEnum.WIN_CONDITION, "CAMPAIGN_AEII_STAGE_7_OBJECTIVE_1");
    }


    @Override
    public void onChapterGameWin() {
        addDialogAndWait(DialogEnum.LOADER_BLUE, "CAMPAIGN_AEII_STAGE_7_MESSAGE_9");
    }


    public void triggerStage1() {
        addUnitAndMove(1, 6, new Site(9, 12), new Site(8, 12), new Site(8, 15));
        addUnitAndMove(1, 1, new Site(9, 12), new Site(8, 12), new Site(8, 14));
        addUnitAndMove(1, 4, new Site(9, 12), new Site(8, 12), new Site(8, 13));
        addUnitAndMove(1, 2, new Site(9, 12), new Site(9, 14));
        sendCommandNow();

        await(4000);

        addDialogAndWait(DialogEnum.FRIEND_UNIT2, "CAMPAIGN_AEII_STAGE_7_MESSAGE_3");
    }

    public void triggerStage2() {
        Unit crystal = null;
        for (Unit unit : currArmy().getUnits()) {
            if (unit.getTypeId() == 11) {
                crystal = unit;
                break;
            }
        }
        if (crystal == null) {
            throw new AncientEmpireException();
        }

        stage++;

        // 飞龙抢夺水晶
        Unit dragon = addNewUnit(9, new Site(crystal.getRow(), 16), 1);
        moveUnit(dragon.getId(), new Site(crystal.getRow(), 16), new Site(crystal.getRow(), crystal.getColumn()));
        sendCommandNow();

        await(1200);
        addDialogAndWait(DialogEnum.FRIEND_UNIT2, "CAMPAIGN_AEII_STAGE_7_MESSAGE_4");
        addDialogAndWait(DialogEnum.FRIEND_UNIT1, "CAMPAIGN_AEII_STAGE_7_MESSAGE_5");

        removeUnit(getArmyUnitIndexByUnitId(crystal.getId()));
        sendCommandNow();

        moveUnit(dragon.getId(), new Site(crystal.getRow(), crystal.getColumn()), new Site(crystal.getRow(), 1));
        removeUnit(getArmyUnitIndexByUnitId(dragon.getId()));
        sendCommandNow();
        await(2800);
        addDialogAndWait(DialogEnum.FRIEND_UNIT2, "CAMPAIGN_AEII_STAGE_7_MESSAGE_5_1");

        addUnitAndMove(1, 7, new Site(10, 1), new Site(10, 6));
        addUnitAndMove(1, 7, new Site(10, 1), new Site(10, 5), new Site(9, 5));
        addUnitAndMove(1, 9, new Site(10, 1), new Site(10, 5), new Site(8, 5));
        addUnitAndMove(1, 1, new Site(10, 1), new Site(10, 4), new Site(8, 4));
        addUnitAndMove(1, 9, new Site(10, 1), new Site(10, 3));
        addUnitAndMove(1, 5, new Site(10, 1), new Site(10, 3), new Site(9, 3));
        addUnitAndMove(1, 9, new Site(10, 1), new Site(10, 2));
        addUnitAndMove(1, 10, new Site(10, 1), new Site(8, 1));
        sendCommandNow();
        await(9500);
        addDialogAndWait(DialogEnum.LOADER_RED, "CAMPAIGN_AEII_STAGE_7_MESSAGE_6");


        addUnitAndMove(1, 1, new Site(15, 14), new Site(15, 13));
        addUnitAndMove(1, 3, new Site(15, 14), new Site(13, 14));
        addUnitAndMove(1, 4, new Site(15, 14), new Site(16, 14));
        addUnitAndMove(1, 7, new Site(15, 14), new Site(15, 15));

        sendCommandNow();
        await(3000);
        addDialogAndWait(DialogEnum.FRIEND_UNIT1, "CAMPAIGN_AEII_STAGE_7_MESSAGE_7");
        addDialogAndWait(DialogEnum.LOADER_BLUE, "CAMPAIGN_AEII_STAGE_7_MESSAGE_8");

        await(500);

        addUnitAndMove(0, 13, new Site(18, 14), new Site(17, 14));
        addUnitAndMove(0, 8, new Site(18, 14), new Site(17, 14), new Site(17, 13));
        addUnitAndMove(0, 9, new Site(18, 14), new Site(17, 14), new Site(17, 15));
        addUnitAndMove(0, 2, new Site(18, 14), new Site(18, 13));
        addUnitAndMove(0, 5, new Site(18, 14), new Site(18, 15));
        sendCommandNow();
        await(4000);
        addDialogAndWait(DialogEnum.LOADER_GREEN, "CAMPAIGN_AEII_STAGE_7_MESSAGE_9");
        addDialogAndWait(DialogEnum.WIN_CONDITION, "CAMPAIGN_AEII_STAGE_7_OBJECTIVE_2");
    }


}
