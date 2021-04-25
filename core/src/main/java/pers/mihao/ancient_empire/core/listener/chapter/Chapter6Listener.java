package pers.mihao.ancient_empire.core.listener.chapter;

import pers.mihao.ancient_empire.core.eums.DialogEnum;

/**
 * 第6章监听处理类
 *
 * @Author mh32736
 * @Date 2021/4/1 9:20
 */
public class Chapter6Listener extends AbstractChapterListener {


    @Override
    protected void initConditions() {

    }

    @Override
    protected void onChapterGameOver() {

    }

    @Override
    public void onChapterGameStart() {
        addDialogAndWait(DialogEnum.FRIEND_UNIT1, "CAMPAIGN_AEII_STAGE_6_MESSAGE_1");
        addDialogAndWait(DialogEnum.WIN_CONDITION, "CAMPAIGN_AEII_STAGE_6_OBJECTIVE");
    }


    @Override
    public void onChapterGameWin() {
        addDialogAndWait(DialogEnum.LOADER_BLUE, "CAMPAIGN_AEII_STAGE_6_MESSAGE_2");
    }


}
