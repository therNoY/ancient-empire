package pers.mihao.ancient_empire.common.log;

import java.util.Map;

/**
 * 自定义解析参数
 * @Author mihao
 * @Date 2021/6/30 10:15
 */
public class DefaultLogParseHandle implements LogParseHandle{

    /**
     * 解析器会根据log的getSet字段设置
     * @param request
     * @param response
     * @param log
     * @return
     */
    @Override
    public Map<String, Object> parseLogField(Object response, Object[] request, AbstractLog log) {

        return null;
    }
}
