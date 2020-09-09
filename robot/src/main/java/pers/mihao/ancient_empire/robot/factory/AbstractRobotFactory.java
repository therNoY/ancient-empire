package pers.mihao.ancient_empire.robot.factory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import pers.mihao.ancient_empire.robot.DefaultRoot;
import pers.mihao.ancient_empire.robot.Robot;

/**
 * 机器人工长接口的抽象实现类
 *
 * @Author mh32736
 * @Date 2020/9/9 20:32
 */
public abstract class AbstractRobotFactory implements RobotFactory {

    private static AtomicInteger threadIndex = new AtomicInteger(0);
    private static String threadName = "robot-";

    ExecutorService robotPool = Executors.newCachedThreadPool(runnable -> {
        Thread thread = new Thread(runnable);
        thread.setName(threadName + threadIndex);
        return thread;
    });


}
