package pers.mihao.ancient_empire.common.task;

/**
 * @Author mihao
 * @Date 2021/7/19 18:55
 */
public interface GroupTaskHandler<T> {

    /**
     * 提交任务 返回组的监听
     * @param groupId
     * @param t
     * @return
     */
    Listener submitTask(String groupId, T t);

}
