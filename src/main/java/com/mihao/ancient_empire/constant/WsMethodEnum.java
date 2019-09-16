package com.mihao.ancient_empire.constant;

import com.mihao.ancient_empire.common.enums.BaseEnum;

/**
 * 通知前端修改 **
 */
public enum WsMethodEnum implements BaseEnum {
    MOVE_AREAS, // 移动范围
    MOVE_PATH, // 移动路径
    MOVE_ACTION, // actions
    ATTACH_AREA, //  攻击范围
    ATTACH_RESULT, // 攻击结果
    END_RESULT, // 攻击结果
    SUMMON_RESULT, // 召唤结果
    UNIT_ACTION, // 可选action
}
