package pers.mihao.ancient_empire.core.listener.chapter;

import pers.mihao.ancient_empire.core.eums.DialogEnum;

/**
 * 第一章监听处理类
 *
 * @Author mh32736
 * @Date 2021/4/1 9:20
 */
public class Chapter7Listener extends AbstractChapterListener {


    @Override
    protected void initConditions() {

    }

    @Override
    protected void onChapterGameOver() {

    }

    @Override
    public void onChapterGameStart() {
        addDialogAndWait(DialogEnum.WIN_CONDITION, "CAMPAIGN_AEII_STAGE_7_OBJECTIVE_1");
        addDialogAndWait(DialogEnum.WIN_CONDITION, "CAMPAIGN_AEII_STAGE_7_OBJECTIVE_2");
    }


    @Override
    public void onChapterGameWin() {
        addDialogAndWait(DialogEnum.LOADER_BLUE, "CAMPAIGN_AEII_STAGE_7_MESSAGE_9");
    }


}
