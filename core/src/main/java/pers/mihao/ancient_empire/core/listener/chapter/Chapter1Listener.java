package pers.mihao.ancient_empire.core.listener.chapter;

import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.core.eums.DialogEnum;

/**
 * 第一章监听处理类
 * @Author mh32736
 * @Date 2021/4/1 9:20
 */
public class Chapter1Listener extends AbstractChapterListener {

    public Chapter1Listener() {
        super.triggerSites = new Site[]{new Site(10, 10)};
        super.maxStage = 2;
    }

    @Override
    public void onChapterGameStart() {
        addDialogAndWait(DialogEnum.WIN_CONDITION, "CAMPAIGN_AEII_STAGE_1_OBJECTIVE");
        gameContext.getHandler().addNewUnit(1, null, 2);
        gameContext.getHandler().addNewUnit(1, null, 1);
        gameContext.getHandler().addNewUnit(1, null, 2);
        addDialogAndWait(DialogEnum.FRIEND_UNIT, "CAMPAIGN_AEII_STAGE_1_MESSAGE_1");

        gameContext.getHandler().showAttachAnim(null, null, null, null, null);
        addDialogAndWait(DialogEnum.FRIEND_UNIT, "CAMPAIGN_AEII_STAGE_1_MESSAGE_2");
        addDialogAndWait(DialogEnum.FRIEND_UNIT, "CAMPAIGN_AEII_STAGE_1_MESSAGE_3");
        addDialogAndWait(DialogEnum.FRIEND_UNIT, "CAMPAIGN_AEII_STAGE_1_MESSAGE_4");
    }




    @Override
    public void onChapterGameWin() {
        addDialogAndWait(DialogEnum.FRIEND_UNIT, "CAMPAIGN_AEII_STAGE_1_MESSAGE_7");
        addDialogAndWait(DialogEnum.FRIEND_UNIT, "CAMPAIGN_AEII_STAGE_1_MESSAGE_8");
        addDialogAndWait(DialogEnum.FRIEND_UNIT, "CAMPAIGN_AEII_STAGE_1_MESSAGE_9");
        addDialogAndWait(DialogEnum.FRIEND_UNIT, "CAMPAIGN_AEII_STAGE_1_MESSAGE_10");
    }


    /**
     * 触发阶段一
     */
    private void triggerStage1() {
        gameContext.getHandler().addNewUnit(1, null, 2);
        gameContext.getHandler().addNewUnit(1, null, 2);
        gameContext.getHandler().addNewUnit(1, null, 2);
        addDialogAndWait(DialogEnum.FRIEND_UNIT, "CAMPAIGN_AEII_STAGE_1_MESSAGE_5");
        addDialogAndWait(DialogEnum.FRIEND_UNIT, "CAMPAIGN_AEII_STAGE_1_MESSAGE_6");
    }
}
