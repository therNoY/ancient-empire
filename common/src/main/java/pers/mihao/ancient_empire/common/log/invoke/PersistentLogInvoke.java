package pers.mihao.ancient_empire.common.log.invoke;

import pers.mihao.ancient_empire.common.log.AbstractLog;

/**
 * @Author mihao
 * @Date 2021/6/30 13:42
 */
public interface PersistentLogInvoke {

    /**
     * 持久化数据
     * @param log
     */
    void doPersistLog(AbstractLog log);

}
