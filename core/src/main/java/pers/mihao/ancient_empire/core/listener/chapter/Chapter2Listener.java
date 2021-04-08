package pers.mihao.ancient_empire.core.listener.chapter;

import pers.mihao.ancient_empire.core.eums.DialogEnum;

/**
 * @Author mh32736
 * @Date 2021/4/1 9:20
 */
public class Chapter2Listener extends AbstractChapterListener {


    @Override
    public void onChapterGameStart() {
        addDialogAndWait(DialogEnum.WIN_CONDITION, "CAMPAIGN_AEII_STAGE_2_OBJECTIVE");
        addDialogAndWait(DialogEnum.FRIEND_UNIT, "CAMPAIGN_AEII_STAGE_2_MESSAGE_1");
        addDialogAndWait(DialogEnum.LOADER_RED, "CAMPAIGN_AEII_STAGE_2_MESSAGE_2");
        addDialogAndWait(DialogEnum.FRIEND_UNIT, "CAMPAIGN_AEII_STAGE_2_MESSAGE_3");
        addDialogAndWait(DialogEnum.LOADER_BLUE, "CAMPAIGN_AEII_STAGE_2_MESSAGE_4");
        addDialogAndWait(DialogEnum.FRIEND_UNIT, "CAMPAIGN_AEII_STAGE_2_MESSAGE_5");
        addDialogAndWait(DialogEnum.FRIEND_UNIT, "CAMPAIGN_AEII_STAGE_2_MESSAGE_6");
        addDialogAndWait(DialogEnum.FRIEND_UNIT, "CAMPAIGN_AEII_STAGE_2_MESSAGE_7");
        addDialogAndWait(DialogEnum.LOADER_BLUE, "CAMPAIGN_AEII_STAGE_2_MESSAGE_8");

    }

    @Override
    public void onChapterGameWin() {
        addDialogAndWait(DialogEnum.LOADER_BLUE, "CAMPAIGN_AEII_STAGE_2_MESSAGE_9");
        addDialogAndWait(DialogEnum.FRIEND_UNIT, "CAMPAIGN_AEII_STAGE_2_MESSAGE_10");
    }
}
