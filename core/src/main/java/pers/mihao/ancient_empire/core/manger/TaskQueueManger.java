package pers.mihao.ancient_empire.core.manger;

import org.springframework.boot.CommandLineRunner;

/**
 * @Author mh32736
 * @Date 2020/9/16 13:49
 */
public interface TaskQueueManger<T> extends CommandLineRunner {

    void handelTask(T t);

    void addTask(T t);
}
