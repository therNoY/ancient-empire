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
    protected void initConditions() {

    }

    @Override
    protected void onChapterGameOver() {

    }

    @Override
    public void onChapterGameStart() {
        addDialogAndWait(DialogEnum.FRIEND_UNIT1, "CAMPAIGN_AEII_STAGE_4_MESSAGE_1");
        addDialogAndWait(DialogEnum.LOADER_BLUE, "CAMPAIGN_AEII_STAGE_4_MESSAGE_2");
        addDialogAndWait(DialogEnum.FRIEND_UNIT2, "CAMPAIGN_AEII_STAGE_4_MESSAGE_3");

        // 移动单位
        moveUnit(new Site(11, 10), new Site(11, 10), new Site(11, 7));
        sendCommandNow();
        waitExecutionOk(5000);

        // 攻击房屋
        showDestroyTown(new Site(10, 5));
        sendCommandNow();
        waitExecutionOk(3000);

        addDialogAndWait(DialogEnum.FRIEND_UNIT2, "CAMPAIGN_AEII_STAGE_4_MESSAGE_4");
        addDialogAndWait(DialogEnum.LOADER_BLUE, "CAMPAIGN_AEII_STAGE_4_MESSAGE_5");

        addDialogAndWait(DialogEnum.WIN_CONDITION, "CAMPAIGN_AEII_STAGE_4_OBJECTIVE");
    }




    @Override
    public void onChapterGameWin() {
        addDialogAndWait(DialogEnum.LOADER_BLUE, "CAMPAIGN_AEII_STAGE_4_MESSAGE_6");
        addDialogAndWait(DialogEnum.LOADER_BLUE, "CAMPAIGN_AEII_STAGE_4_MESSAGE_7");
    }


}
