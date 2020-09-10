package pers.mihao.ancient_empire.robot;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import pers.mihao.ancient_empire.common.annotation.Manger;
import pers.mihao.ancient_empire.robot.factory.DefaultRobotFactory;

/**
 * @Author mh32736
 * @Date 2020/9/10 8:39
 */
@Manger
public class RobotManger {

    private AtomicInteger threadIndex = new AtomicInteger(0);
    private String threadName = "robot-";

    /**
     * 机器人执行任务线程池
     */
    ExecutorService robotPool = new ThreadPoolExecutor(
        Runtime.getRuntime().availableProcessors(),
        Runtime.getRuntime().availableProcessors() * 10,
        30, TimeUnit.MINUTES,
        new SynchronousQueue<>(),
        runnable -> {
            Thread thread = new Thread(runnable);
            thread.setName(threadName + threadIndex);
            return thread;
        });


    /**
     * 开始执行一个任务
     */
    public void startRobot() {
        Robot robot = DefaultRobotFactory.createRobot();
        robotPool.submit(robot);
    }


}
