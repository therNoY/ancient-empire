package pers.mihao.ancient_empire.core.eums;

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
    REPAIR_RESULT, // 占领结果
    UNIT_ACTION, // 可选action
    BUY_UNIT, // 购买单位


    NEW_ROUND, // 开始新的回合

    ERROR, // 同一错误处理
}
