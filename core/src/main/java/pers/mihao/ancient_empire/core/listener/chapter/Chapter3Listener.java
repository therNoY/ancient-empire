package pers.mihao.ancient_empire.core.listener.chapter;

import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.core.eums.DialogEnum;

/**
 * 第一章监听处理类
 * @Author mh32736
 * @Date 2021/4/1 9:20
 */
public class Chapter3Listener extends AbstractChapterListener {

    public Chapter3Listener() {
        super.triggerSites = new Site[]{new Site(10, 10), new Site(11, 11)};
        super.maxStage = 2;
    }

    @Override
    public void onChapterGameStart() {
        addDialogAndWait(DialogEnum.WIN_CONDITION, "CAMPAIGN_AEII_STAGE_3_OBJECTIVE");
        addDialogAndWait(DialogEnum.FRIEND_UNIT, "CAMPAIGN_AEII_STAGE_3_MESSAGE_1");
        addDialogAndWait(DialogEnum.LOADER_BLUE, "CAMPAIGN_AEII_STAGE_3_MESSAGE_2");
        addDialogAndWait(DialogEnum.FRIEND_UNIT, "CAMPAIGN_AEII_STAGE_3_MESSAGE_3");
    }




    @Override
    public void onChapterGameWin() {
        addDialogAndWait(DialogEnum.LOADER_BLUE, "CAMPAIGN_AEII_STAGE_3_MESSAGE_7");
    }

    /**
     * 触发阶段一
     */
    private void triggerStage1() {
        gameContext.getHandler().addNewUnit(1, null, 2);
        gameContext.getHandler().addNewUnit(1, null, 2);
        gameContext.getHandler().addNewUnit(1, null, 2);
        addDialogAndWait(DialogEnum.FRIEND_UNIT, "CAMPAIGN_AEII_STAGE_3_MESSAGE_4");
    }


    /**
     * 触发阶段一
     */
    private void triggerStage2() {
        gameContext.getHandler().addNewUnit(1, null, 1);
        gameContext.getHandler().addNewUnit(1, null, 1);
        gameContext.getHandler().addNewUnit(1, null, 1);

        addDialogAndWait(DialogEnum.FRIEND_UNIT, "CAMPAIGN_AEII_STAGE_3_MESSAGE_5");
        addDialogAndWait(DialogEnum.FRIEND_ELF, "CAMPAIGN_AEII_STAGE_3_MESSAGE_6");
    }
}
