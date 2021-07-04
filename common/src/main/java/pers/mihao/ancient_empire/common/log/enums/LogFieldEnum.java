package pers.mihao.ancient_empire.common.log.enums;

import pers.mihao.ancient_empire.common.enums.BaseEnum;

/**
 * 支持的扩展字段
 *
 * @Author mihao
 * @Date 2021/7/1 10:45
 */
public enum LogFieldEnum implements BaseEnum {

    /**
     * 日志ID
     */
    LOG_ID(true),

    /**
     * 当前操做的ID
     */
    TRIGGER_USER_ID(true),

    CREATE_TIME(true),

    EXEC_TIME(true),

    SERVICE_NAME(true),

    REQUEST(false),

    RESPONSE(false),

    INVOKE_METHOD(true);

    /**
     * 是否默认支持
     */
    boolean defaultAdd;

    LogFieldEnum(boolean defaultAdd) {
        this.defaultAdd = defaultAdd;
    }

    public boolean isDefaultAdd() {
        return defaultAdd;
    }
}
