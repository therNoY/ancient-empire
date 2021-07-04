package pers.mihao.ancient_empire.common.log;

import java.util.Arrays;
import pers.mihao.ancient_empire.common.log.enums.LogFieldEnum;

/**
 * 记录请求参数日志
 * @Author mihao
 * @Date 2021/7/2 13:04
 */
public class RecordRequestLog extends AbstractLog{

    @Override
    public String getTableName() {
        return null;
    }

    @Override
    public LogFieldEnum[] getExtLogField() {
        LogFieldEnum[] list = super.getExtLogField();
        LogFieldEnum[] returnRes =  Arrays.copyOf(list, list.length + 1);
        returnRes[list.length] = LogFieldEnum.REQUEST;
        return returnRes;
    }
}
