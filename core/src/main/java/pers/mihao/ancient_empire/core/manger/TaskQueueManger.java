package pers.mihao.ancient_empire.core.manger;

import org.springframework.boot.CommandLineRunner;

/**
 * @Author mh32736
 * @Date 2020/9/16 13:49
 */
public interface TaskQueueManger<T> extends CommandLineRunner {

    /**
     * 处理事件
     * @param t
     */
    void handelTask(T t);

    /**
     * 添加任务
     * @param t
     */
    void addTask(T t);
}
