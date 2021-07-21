package pers.mihao.ancient_empire.common.task;

/**
 * @Author mihao
 * @Date 2021/7/17 16:45
 */
public interface GroupTask<T extends GroupCommend> {

    /**
     * 获取第一个
     * @return
     */
    T getFirst();

    /**
     * 添加任务
     * @param t
     */
    void addTask(T t);

    /**
     * 是否还有任务
     * @return
     */
    boolean hasTask();


    /**
     * 移除第一个
     */
    T removeFirst();
}
