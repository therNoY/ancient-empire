package pers.mihao.ancient_empire.core.listener.chapter;

import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.base.util.AppUtil;
import pers.mihao.ancient_empire.core.dto.ArmyUnitIndexDTO;
import pers.mihao.ancient_empire.core.eums.DialogEnum;

/**
 * @Author mh32736
 * @Date 2021/4/1 9:20
 */
public class Chapter2Listener extends AbstractChapterListener {


    @Override
    protected void initConditions() {

    }

    @Override
    protected void onChapterGameOver() {

    }

    @Override
    public void onChapterGameStart() {
        addDialogAndWait(DialogEnum.LOADER_GREEN, "CAMPAIGN_AEII_STAGE_2_MESSAGE_1");
        addDialogAndWait(DialogEnum.LOADER_RED, "CAMPAIGN_AEII_STAGE_2_MESSAGE_2");
        addDialogAndWait(DialogEnum.FRIEND_UNIT1, "CAMPAIGN_AEII_STAGE_2_MESSAGE_3");
        addUnitAndMove(1, 1, new Site(4, 8), new Site(4, 7), new Site(1, 7));
        removeUnit(new ArmyUnitIndexDTO(1, 2));
        addUnitAndMove(1, 11, new Site(4, 8), new Site(4, 7), new Site(1, 7));
        removeUnit(new ArmyUnitIndexDTO(1, 2));
        ArmyUnitIndexDTO attachIndex = new ArmyUnitIndexDTO(1, 2);
        addUnitAndMove(1, 1, new Site(4, 8), new Site(4, 7), new Site(2, 7));

        sendCommandNow();
        await(3000);
        addDialogAndWait(DialogEnum.FRIEND_UNIT2, "CAMPAIGN_AEII_STAGE_2_MESSAGE_4");

        ArmyUnitIndexDTO beAttchIndex = new ArmyUnitIndexDTO(0, 3);
        addUnitAndMove(0, 1, new Site(4, 8), new Site(4, 7), new Site(3, 7));

        changeUnitPoint(attachIndex);

        // 生成攻击事件
        showAttachAnim(AppUtil.getArrayByInt(-1, 100), attachIndex, beAttchIndex);
        sendUnitDeadCommend(getUnitInfoByIndex(beAttchIndex), beAttchIndex);
        sendCommandNow();

        await(1000);

        addDialogAndWait(DialogEnum.FRIEND_UNIT2, "CAMPAIGN_AEII_STAGE_2_MESSAGE_5");

        addDialogAndWait(DialogEnum.FRIEND_UNIT1, "CAMPAIGN_AEII_STAGE_2_MESSAGE_6");
        addDialogAndWait(DialogEnum.LOADER_GREEN, "CAMPAIGN_AEII_STAGE_2_MESSAGE_7");
        changeCurrPoint(new Site(6, 4));
        sendCommandNow();
        await(100);
        addDialogAndWait(DialogEnum.LOADER_GREEN, "CAMPAIGN_AEII_STAGE_2_MESSAGE_8");
        addDialogAndWait(DialogEnum.WIN_CONDITION, "CAMPAIGN_AEII_STAGE_2_OBJECTIVE");
    }

    @Override
    public void onChapterGameWin() {
        addDialogAndWait(DialogEnum.LOADER_BLUE, "CAMPAIGN_AEII_STAGE_2_MESSAGE_9");
        addDialogAndWait(DialogEnum.FRIEND_UNIT1, "CAMPAIGN_AEII_STAGE_2_MESSAGE_10");
    }
}
