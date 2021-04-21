package pers.mihao.ancient_empire.core.listener.chapter;

import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.core.eums.DialogEnum;

/**
 * 第一章监听处理类
 *
 * @Author mh32736
 * @Date 2021/4/1 9:20
 */
public class Chapter5Listener extends AbstractChapterListener {


    @Override
    protected void initConditions() {

    }

    @Override
    protected void onChapterGameOver() {

    }

    @Override
    public void onChapterGameStart() {
        addDialogAndWait(DialogEnum.WIN_CONDITION, "CAMPAIGN_AEII_STAGE_5_OBJECTIVE");
        addDialogAndWait(DialogEnum.FRIEND_UNIT1, "CAMPAIGN_AEII_STAGE_5_MESSAGE_1");
        addDialogAndWait(DialogEnum.LOADER_BLUE, "CAMPAIGN_AEII_STAGE_5_MESSAGE_2");
    }


    @Override
    public void onChapterGameWin() {
    }

    /**
     * 触发阶段一
     */
    private void triggerStage1() {
        addNewUnit(1, null, 2);
        addNewUnit(1, null, 2);
        addNewUnit(1, null, 2);
        addDialogAndWait(DialogEnum.FRIEND_UNIT1, "CAMPAIGN_AEII_STAGE_5_MESSAGE_3");
    }


    /**
     * 触发阶段一
     */
    private void triggerStage2() {
        addNewUnit(1, null, 1);
        addNewUnit(1, null, 1);
        addNewUnit(1, null, 1);

        addDialogAndWait(DialogEnum.FRIEND_UNIT1, "CAMPAIGN_AEII_STAGE_5_MESSAGE_4");
    }
}
