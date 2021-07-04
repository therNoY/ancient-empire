package pers.mihao.ancient_empire.core.robot;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.beans.factory.annotation.Autowired;
import pers.mihao.ancient_empire.common.annotation.Manger;
import pers.mihao.ancient_empire.common.util.ThreadPoolNameUtil;
import pers.mihao.ancient_empire.core.manger.GameContext;
import pers.mihao.ancient_empire.core.manger.GameCoreManger;
import pers.mihao.ancient_empire.core.robot.factory.DefaultRobotFactory;

/**
 * 构造人机 处理人机行动
 * @Author mh32736
 * @Date 2020/9/10 8:39
 */
@Manger
public class RobotManger {

    private AtomicInteger threadIndex = new AtomicInteger(0);
    private String threadName = "robot";

    @Autowired
    GameCoreManger gameCoreManger;

    /**
     * 机器人执行任务线程池
     */
    ExecutorService robotPool = new ThreadPoolExecutor(
        1,
        Runtime.getRuntime().availableProcessors() * 10,
        30, TimeUnit.MINUTES,
        new SynchronousQueue<>(),
        runnable -> {
            Thread thread = new Thread(runnable);
            thread.setName(ThreadPoolNameUtil.getThreadName(threadName));
            return thread;
        });


    /**
     * 开始执行一个任务
     */
    public void startRobot(GameContext gameContext) {
        AbstractRobot robot = DefaultRobotFactory.createRobot(gameContext);
        robotPool.submit(robot);
    }


}
