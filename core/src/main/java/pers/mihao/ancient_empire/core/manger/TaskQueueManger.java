package pers.mihao.ancient_empire.core.manger;

import org.springframework.boot.CommandLineRunner;
import pers.mihao.ancient_empire.common.annotation.KnowledgePoint;

/**
 * @Author mh32736
 * @Date 2020/9/16 13:49
 */
@KnowledgePoint("项目运行完毕就执行的方法可以实现CommandLineRunner接口")
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
