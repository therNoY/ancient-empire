package pers.mihao.ancient_empire.common.log.invoke;

import pers.mihao.ancient_empire.common.log.AbstractLog;

/**
 * @Author mh32736
 * @Date 2021/6/30 13:42
 */
public interface PersistentLogInvoke {

    /**
     * 持久化数据
     * @param args
     * @param res
     * @param log
     */
    void doPersistLog(Object[] args, Object res, AbstractLog log);

}
