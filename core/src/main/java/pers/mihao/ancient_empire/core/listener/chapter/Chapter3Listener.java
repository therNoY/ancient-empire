package pers.mihao.ancient_empire.core.listener.chapter;

import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.core.eums.DialogEnum;
import pers.mihao.ancient_empire.core.listener.chapter.enums.TriggerTypeEnum;

/**
 * 第三章监听处理类
 *
 * @Author mihao
 * @Date 2021/4/1 9:20
 */
public class Chapter3Listener extends AbstractChapterListener {


    @Override
    protected void initConditions() {
        TriggerCondition condition1 = new TriggerCondition(TriggerTypeEnum.IN_AREA);
        condition1.setMinSite(new Site(1, 1));
        condition1.setMaxSite(new Site(11, 10));

        TriggerCondition condition2 = new TriggerCondition(TriggerTypeEnum.IN_AREA);
        condition2.setMinSite(new Site(1, 1));
        condition2.setMaxSite(new Site(5, 10));
        triggerConditions = new TriggerCondition[]{condition1, condition2};
    }

    @Override
    protected void onChapterGameOver() {

    }

    @Override
    public void onChapterGameStart() {
        addDialogAndWait(DialogEnum.FRIEND_UNIT2, "CAMPAIGN_AEII_STAGE_3_MESSAGE_1");
        addDialogAndWait(DialogEnum.LOADER_BLUE, "CAMPAIGN_AEII_STAGE_3_MESSAGE_2");
        addDialogAndWait(DialogEnum.FRIEND_UNIT2, "CAMPAIGN_AEII_STAGE_3_MESSAGE_3");
        addDialogAndWait(DialogEnum.WIN_CONDITION, "CAMPAIGN_AEII_STAGE_3_OBJECTIVE");
    }


    @Override
    public void onChapterGameWin() {
        awaitTime(800);
        addDialogAndWait(DialogEnum.LOADER_BLUE, "CAMPAIGN_AEII_STAGE_3_MESSAGE_7");
    }

    /**
     * 触发阶段一
     */
    public void triggerStage1() {
        // 出现狼群
        addUnitSendNow(6, new Site(9, 1), 1);
        addUnitSendNow(6, new Site(8, 2), 1);
        addUnitSendNow(6, new Site(7, 9), 1);

        addDialogAndWait(DialogEnum.FRIEND_UNIT2, "CAMPAIGN_AEII_STAGE_3_MESSAGE_4");
        // 出现水元素 和 小精灵
        Unit water1 = addUnitAndMove(1, 3, new Site(9, 5), new Site(9, 4));
        Unit elf = addUnitAndMove(1, 5, new Site(9, 5), new Site(8, 5));
        Unit water2 = addUnitAndMove(1, 3, new Site(9, 5), new Site(9, 6));

        sendCommandNow();

        addDialogAndWait(DialogEnum.FRIEND_UNIT2, "CAMPAIGN_AEII_STAGE_3_MESSAGE_5");
        addDialogAndWait(DialogEnum.FRIEND_ELF, "CAMPAIGN_AEII_STAGE_3_MESSAGE_6");

        changeUnitArmy(water1, 0);
        changeUnitArmy(elf, 0);
        changeUnitArmy(water2, 0);

        sendCommandNow();
        waitExecutionOk(8000);

        addDialogAndWait(DialogEnum.LOADER_BLUE, "CAMPAIGN_AEII_STAGE_3_MESSAGE_7");
    }





    /**
     * 触发阶段二
     */
    public void triggerStage2() {
        addUnitSendNow(6, new Site(3, 2), 1);
        addUnitSendNow(5, new Site(2, 3), 1);
        addUnitSendNow(6, new Site(3, 4), 1);
    }
}
