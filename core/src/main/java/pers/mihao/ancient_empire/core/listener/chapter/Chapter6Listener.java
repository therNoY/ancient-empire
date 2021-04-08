package pers.mihao.ancient_empire.core.listener.chapter;

import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.core.eums.DialogEnum;

/**
 * 第一章监听处理类
 *
 * @Author mh32736
 * @Date 2021/4/1 9:20
 */
public class Chapter6Listener extends AbstractChapterListener {


    @Override
    public void onChapterGameStart() {
        addDialogAndWait(DialogEnum.WIN_CONDITION, "CAMPAIGN_AEII_STAGE_6_OBJECTIVE");
        addDialogAndWait(DialogEnum.FRIEND_UNIT, "CAMPAIGN_AEII_STAGE_6_MESSAGE_1");
    }


    @Override
    public void onChapterGameWin() {
        addDialogAndWait(DialogEnum.LOADER_BLUE, "CAMPAIGN_AEII_STAGE_6_MESSAGE_2");
    }


}
