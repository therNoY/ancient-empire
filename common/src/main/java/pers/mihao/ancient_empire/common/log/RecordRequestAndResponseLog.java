package pers.mihao.ancient_empire.common.log;

import java.util.Arrays;
import pers.mihao.ancient_empire.common.log.enums.LogFieldEnum;

/**
 * 记录请求参数和返回参数的日志
 * @Author mh32736
 * @Date 2021/7/2 13:04
 */
public class RecordRequestAndResponseLog extends AbstractLog{

    @Override
    public String getTableName() {
        return null;
    }

    @Override
    public LogFieldEnum[] getExtLogField() {
        LogFieldEnum[] list = super.getExtLogField();
        LogFieldEnum[] returnRes =  Arrays.copyOf(list, list.length + 2);
        returnRes[list.length] = LogFieldEnum.REQUEST;
        returnRes[list.length + 1] = LogFieldEnum.RESPONSE;
        return returnRes;
    }
}
