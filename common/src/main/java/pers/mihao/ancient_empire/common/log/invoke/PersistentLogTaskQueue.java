package pers.mihao.ancient_empire.common.log.invoke;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import pers.mihao.ancient_empire.common.util.ThreadPoolNameUtil;

/**
 * @Author mihao
 * @Date 2021/6/30 19:40
 */
public class PersistentLogTaskQueue {

    private static PersistentLogTaskQueue taskQueue;

    /**
     * 异步写日志线程池
     */
    private ExecutorService invokePersistentLogPoll;

    private PersistentLogTaskQueue() {
    }

    public static PersistentLogTaskQueue getInstance() {
        if (taskQueue == null) {
            synchronized (PersistentLogTaskQueue.class) {
                if (taskQueue == null) {
                    taskQueue = new PersistentLogTaskQueue();
                    taskQueue.invokePersistentLogPoll = new ThreadPoolExecutor(
                        4,
                        4,
                        0, TimeUnit.MINUTES,
                        new LinkedBlockingQueue<>(),
                        runnable -> {
                            Thread thread = new Thread(runnable);
                            thread.setName(ThreadPoolNameUtil.getThreadName("persistentLog"));
                            return thread;
                        });
                }
            }

        }
        return taskQueue;
    }

    /**
     * 增加日志
     *
     * @param log
     */
    public void addTask(Runnable runnable) {
        invokePersistentLogPoll.execute(runnable);
    }
}
