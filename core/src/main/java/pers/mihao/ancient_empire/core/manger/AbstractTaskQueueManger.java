package pers.mihao.ancient_empire.core.manger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author mh32736
 * @Date 2020/9/16 13:43
 */
public abstract class AbstractTaskQueueManger<T> implements TaskQueueManger<T> {

    private AtomicInteger threadIndex = new AtomicInteger(0);
    private String threadName = "handleTask-";

    /**
     * 事件处理线程池
     */
    protected ExecutorService taskPool = new ThreadPoolExecutor(
        1,
        Runtime.getRuntime().availableProcessors(),
        0, TimeUnit.MINUTES,
        new LinkedBlockingQueue<>(10),
        runnable -> {
            Thread thread = new Thread(runnable);
            thread.setName(threadName + threadIndex);
            return thread;
        });

    /**
     * 添加任务到队列
     * @param t
     */
    @Override
    public final void addTask(T t) {
        taskPool.submit(new TaskHandel(t));
    }

    /**
     * 可以修改任务名字
     * @param name
     */
    protected void setThreadName(String name) {
        this.threadName = name;
    }

    /**
     * 任务处理
     */
    class TaskHandel implements Runnable {

        T t;

        public TaskHandel(T t) {
            this.t = t;
        }

        @Override
        public void run() {
            handelTask(t);
        }
    }

    @Override
    public void run(String... args) throws Exception {
    }

}
