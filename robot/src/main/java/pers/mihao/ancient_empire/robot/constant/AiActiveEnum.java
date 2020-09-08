package pers.mihao.ancient_empire.robot.constant;

import com.mihao.ancient_empire.common.enums.BaseEnum;
import pers.mihao.ancient_empire.common.constant.ActionEnum;

public enum AiActiveEnum implements BaseEnum {

    SELECT_UNIT, // 1.第一步是选择单位
    MOVE_UNIT, // 2. 第二步是准备移动选择的单位
    END_TURN, // 结束回合
    BUY_UNIT,
    SUMMON, // 召唤
    REPAIR, // 修复
    OCCUPIED, // 占领
    HEAL, // 治疗
    ATTACH, // 攻击
    END, // 结束
}
