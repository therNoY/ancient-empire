package pers.mihao.ancient_empire.core.listener.chapter;

import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.base.util.AppUtil;
import pers.mihao.ancient_empire.core.dto.ArmyUnitIndexDTO;
import pers.mihao.ancient_empire.core.eums.DialogEnum;
import pers.mihao.ancient_empire.core.listener.chapter.enums.TriggerTypeEnum;

/**
 * 第一章监听处理类
 * @Author mihao
 * @Date 2021/4/1 9:20
 */
public class Chapter1Listener extends AbstractChapterListener {


    @Override
    protected void initConditions() {
        TriggerCondition condition = new TriggerCondition(TriggerTypeEnum.ALL_DEAD);
//        TriggerCondition condition = new TriggerCondition(TriggerTypeEnum.ANY_DONE);
//        TriggerCondition condition = new TriggerCondition(TriggerTypeEnum.ANY_DEAD);
        triggerConditions = new TriggerCondition[]{condition};
    }

    @Override
    public void onChapterGameStart() {
        // 守城单位被攻击
        ArmyUnitIndexDTO attachIndex, beAttchIndex;
        attachIndex = new ArmyUnitIndexDTO(1, 0);
        beAttchIndex = new ArmyUnitIndexDTO(0, 3);
        changeUnitPoint(beAttchIndex);
        sendCommandNow();
        addDialogAndWait(DialogEnum.FRIEND_UNIT1, "CAMPAIGN_AEII_STAGE_1_MESSAGE_1");
        awaitTime(500);
        changeUnitPoint(attachIndex);
        sendCommandNow();
        // 生成攻击事件
        showAttachAnim(AppUtil.getArrayByInt(-1, 100), attachIndex, beAttchIndex);
        addUnitDeadCommend(getUnitInfoByIndex(beAttchIndex), beAttchIndex);
        sendCommandNow();
        waitExecutionOk(5000);
        // 对话
        addDialogAndWait(DialogEnum.FRIEND_UNIT1, "CAMPAIGN_AEII_STAGE_1_MESSAGE_2");
        addDialogAndWait(DialogEnum.LOADER_BLUE, "CAMPAIGN_AEII_STAGE_1_MESSAGE_3");
        addDialogAndWait(DialogEnum.FRIEND_UNIT2, "CAMPAIGN_AEII_STAGE_1_MESSAGE_4");

        addDialogAndWait(DialogEnum.WIN_CONDITION, "CAMPAIGN_AEII_STAGE_1_OBJECTIVE");
    }




    @Override
    public void onChapterGameWin() {
        addDialogAndWait(DialogEnum.FRIEND_UNIT1, "CAMPAIGN_AEII_STAGE_1_MESSAGE_7");
        addDialogAndWait(DialogEnum.LOADER_BLUE, "CAMPAIGN_AEII_STAGE_1_MESSAGE_8");
        addDialogAndWait(DialogEnum.FRIEND_UNIT1, "CAMPAIGN_AEII_STAGE_1_MESSAGE_9");
        addDialogAndWait(DialogEnum.LOADER_BLUE, "CAMPAIGN_AEII_STAGE_1_MESSAGE_10");
    }

    @Override
    protected void onChapterGameOver() {

    }

    /**
     * 触发阶段一
     */
    public void triggerStage1() {
        // 定时一段时间
        waitExecutionOk(1000);
        addUnitAndMove(1, 2, new Site(2, 2), new Site(2, 3));
        addUnitAndMove(1, 1, new Site(11, 11), new Site(11, 10));
        sendCommandNow();

        waitExecutionOk(5000);
        addDialogAndWait(DialogEnum.FRIEND_UNIT1, "CAMPAIGN_AEII_STAGE_1_MESSAGE_5");
        addDialogAndWait(DialogEnum.FRIEND_UNIT1, "CAMPAIGN_AEII_STAGE_1_MESSAGE_6");
    }




}
