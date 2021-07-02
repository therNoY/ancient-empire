package pers.mihao.ancient_empire.base.log;

import com.alibaba.fastjson.JSONArray;
import java.util.HashMap;
import java.util.Map;
import pers.mihao.ancient_empire.common.log.AbstractLog;
import pers.mihao.ancient_empire.common.log.LogParseHandle;

/**
 * 用户操做地图日志表
 * @Author mh32736
 * @Date 2021/6/30 9:56
 */
public class UserMapOperationLog extends AbstractLog {

    @Override
    public String getTableName() {
        return "user_map_operation_log";
    }

    @Override
    public Class<? extends LogParseHandle> getExtendLogParseHandle() {
        return UserMapOperationLogHandle.class;
    }

    public static class UserMapOperationLogHandle implements LogParseHandle{

        @Override
        public Map<String, Object> parseLogField(Object response, Object[] request, AbstractLog log) {
            Map<String, Object> map = new HashMap<>(16);
            map.put("request", JSONArray.toJSONString(request));
            return map;
        }
    }
}
