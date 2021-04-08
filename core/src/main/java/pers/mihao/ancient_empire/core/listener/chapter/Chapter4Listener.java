package pers.mihao.ancient_empire.core.listener.chapter;

import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.core.eums.DialogEnum;

/**
 * 第一章监听处理类
 * @Author mh32736
 * @Date 2021/4/1 9:20
 */
public class Chapter4Listener extends AbstractChapterListener {

    @Override
    public void onChapterGameStart() {
        addDialogAndWait(DialogEnum.WIN_CONDITION, "CAMPAIGN_AEII_STAGE_4_OBJECTIVE");
        addDialogAndWait(DialogEnum.FRIEND_UNIT, "CAMPAIGN_AEII_STAGE_4_MESSAGE_1");
        addDialogAndWait(DialogEnum.LOADER_BLUE, "CAMPAIGN_AEII_STAGE_4_MESSAGE_2");
        addDialogAndWait(DialogEnum.FRIEND_UNIT, "CAMPAIGN_AEII_STAGE_4_MESSAGE_3");
        addDialogAndWait(DialogEnum.FRIEND_UNIT, "CAMPAIGN_AEII_STAGE_4_MESSAGE_4");
        addDialogAndWait(DialogEnum.FRIEND_UNIT, "CAMPAIGN_AEII_STAGE_4_MESSAGE_5");
    }




    @Override
    public void onChapterGameWin() {
        addDialogAndWait(DialogEnum.LOADER_BLUE, "CAMPAIGN_AEII_STAGE_4_MESSAGE_6");
        addDialogAndWait(DialogEnum.LOADER_BLUE, "CAMPAIGN_AEII_STAGE_4_MESSAGE_7");
    }


}