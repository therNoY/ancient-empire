package pers.mihao.ancient_empire.base.log;

import pers.mihao.ancient_empire.common.log.RecordRequestLog;

/**
 * 用户操做地图日志表
 * @Author mihao
 * @Date 2021/6/30 9:56
 */
public class UserMapOperationLog extends RecordRequestLog {

    @Override
    public String getTableName() {
        return "user_map_operation_log";
    }
}
