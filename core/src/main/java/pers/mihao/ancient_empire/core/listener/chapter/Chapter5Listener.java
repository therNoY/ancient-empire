package pers.mihao.ancient_empire.core.listener.chapter;

import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.core.eums.DialogEnum;
import pers.mihao.ancient_empire.core.listener.chapter.enums.TriggerTypeEnum;

/**
 * 索林小径
 *
 * @Author mihao
 * @Date 2021/4/1 9:20
 */
public class Chapter5Listener extends AbstractChapterListener {


    @Override
    protected void initConditions() {
        TriggerCondition condition1 = new TriggerCondition(TriggerTypeEnum.IN_AREA);
        condition1.setMinSite(new Site(1, 1));
        condition1.setMaxSite(new Site(5, 7));

        TriggerCondition condition2 = new TriggerCondition(TriggerTypeEnum.IN_AREA);
        condition2.setMinSite(new Site(9, 1));
        condition2.setMaxSite(new Site(12, 9));

        TriggerCondition condition3 = new TriggerCondition(TriggerTypeEnum.IN_AREA);
        condition3.setMinSite(new Site(6, 9));
        condition3.setMaxSite(new Site(12, 14));

        TriggerCondition condition4 = new TriggerCondition(TriggerTypeEnum.IN_AREA);
        condition4.setMinSite(new Site(6, 15));
        condition4.setMaxSite(new Site(12, 20));
        triggerConditions = new TriggerCondition[]{condition1, condition2, condition3, condition4};


    }

    @Override
    protected void onChapterGameOver() {

    }

    @Override
    public void onChapterGameStart() {

        addDialogAndWait(DialogEnum.FRIEND_UNIT2, "CAMPAIGN_AEII_STAGE_5_MESSAGE_1");
        addDialogAndWait(DialogEnum.LOADER_BLUE, "CAMPAIGN_AEII_STAGE_5_MESSAGE_2");

        addDialogAndWait(DialogEnum.WIN_CONDITION, "CAMPAIGN_AEII_STAGE_5_OBJECTIVE");
    }


    @Override
    public void onChapterGameWin() {
    }

    /**
     * 触发阶段1
     */
    public void triggerStage1() {
        addUnitAndMove(1, 2, new Site(5, 5), new Site(3, 5), new Site(3, 6));
        addUnitAndMove(1, 12, new Site(5, 5), new Site(2, 5));
        addUnitAndMove(1, 12, new Site(5, 5), new Site(4, 5));
        sendCommandNow();

        waitExecutionOk(9000);

        addDialogAndWait(DialogEnum.FRIEND_UNIT2, "CAMPAIGN_AEII_STAGE_5_MESSAGE_3");
    }


    /**
     * 触发阶段2
     */
    public void triggerStage2() {
        addUnitAndMove(1, 2, new Site(11, 7), new Site(11, 8), new Site(10, 8));
        addUnitAndMove(1, 6, new Site(11, 7), new Site(11, 8), new Site(9, 8));
        addUnitAndMove(1, 6, new Site(11, 7), new Site(11, 6));
        sendCommandNow();

        waitExecutionOk(9000);

        addDialogAndWait(DialogEnum.FRIEND_UNIT2, "CAMPAIGN_AEII_STAGE_5_MESSAGE_3_1");
    }

    /**
     * 触发阶段3
     */
    public void triggerStage3() {
        addUnitAndMove(1, 7, new Site(6, 13), new Site(7, 13));
        addUnitAndMove(1, 6, new Site(6, 13), new Site(6, 12));
        addUnitAndMove(1, 6, new Site(6, 13), new Site(8, 13));
        sendCommandNow();

        waitExecutionOk(12000);

        addDialogAndWait(DialogEnum.FRIEND_UNIT2, "CAMPAIGN_AEII_STAGE_5_MESSAGE_3_2");
    }

    /**
     * 触发阶段4
     */
    public void triggerStage4() {
        addUnitAndMove(1, 7, new Site(9, 19), new Site(11, 19) , new Site(11, 18));
        addUnitAndMove(1, 6, new Site(9, 19), new Site(11, 19) , new Site(11, 17));
        addUnitAndMove(1, 6, new Site(9, 19), new Site(11, 19));
        addUnitAndMove(1, 2, new Site(9, 19), new Site(10, 19));
        sendCommandNow();

        waitExecutionOk(15000);

        addDialogAndWait(DialogEnum.FRIEND_UNIT2, "CAMPAIGN_AEII_STAGE_5_MESSAGE_4");
    }
}
