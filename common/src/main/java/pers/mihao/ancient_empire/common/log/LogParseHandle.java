package pers.mihao.ancient_empire.common.log;

import java.util.Map;

/**
 * @Author mh32736
 * @Date 2021/6/30 10:14
 */
public interface LogParseHandle {


    /**
     * 解析日志字段
     * @param request
     * @param response
     * @return
     */
    Map<String, Object> parseLogField(Object response, Object[] request, AbstractLog log);

}
