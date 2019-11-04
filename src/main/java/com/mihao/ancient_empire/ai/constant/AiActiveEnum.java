package com.mihao.ancient_empire.ai.constant;

import com.mihao.ancient_empire.common.enums.BaseEnum;
import com.mihao.ancient_empire.constant.ActionEnum;

public enum AiActiveEnum implements BaseEnum {

    SELECT_UNIT, // 1.第一步是选择单位
    MOVE_UNIT, // 2. 第二步是准备移动选择的单位
                // 3.第三步是移动后单位的行动
    END_TURN, // 结束回合
    SUMMON, // 召唤
    REPAIR, // 修复
    OCCUPIED, // 占领
    HEAL, // 治疗
    ATTACH, // 攻击
    END, // 结束
}
