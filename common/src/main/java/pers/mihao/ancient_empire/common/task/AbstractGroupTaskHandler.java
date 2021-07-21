package pers.mihao.ancient_empire.common.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.mihao.ancient_empire.common.util.ThreadPoolNameUtil;

/**
 * @Author mihao
 * @Date 2021/7/17 16:48
 */
public abstract class AbstractGroupTaskHandler<T extends GroupCommend> implements GroupTaskHandler<T> {

    Logger log = LoggerFactory.getLogger(AbstractGroupTaskHandler.class);

    /**
     * 分组命令
     */
    Map<String, GroupTaskWrapper> groupTaskMap;

    private ExecutorService threadPoolExecutor;

    Object lock = new Object();

    volatile boolean working = true;

    Thread invokeTaskLoop = null;

    {
        groupTaskMap = new ConcurrentHashMap<>(16);
        if ((threadPoolExecutor = getExecTaskService()) == null) {
            // 设置默认线程池
            threadPoolExecutor = new ThreadPoolExecutor(
                4,
                4,
                0, TimeUnit.MINUTES,
                new LinkedBlockingQueue<>(),
                runnable -> {
                    Thread thread = new Thread(runnable);
                    thread.setName(ThreadPoolNameUtil.getThreadName("execAsyncGroupTask"));
                    return thread;
                });
        }
    }

    @Override
    public Listener submitTask(String groupId, T t) {
        GroupTaskWrapper groupTask = groupTaskMap.get(groupId);
        if (groupTask == null) {
            groupTask = createGroupTaskWrapper(groupId);
            groupTaskMap.put(groupId, groupTask);
        }
        groupTask.addTask(t);
        if (invokeTaskLoop == null) {
            createLoopThread();
        }
        if (!working) {
            synchronized (lock) {
                lock.notifyAll();
            }
        }
        return groupTask.listener;
    }

    private void createLoopThread() {
        invokeTaskLoop = new Thread(new LoopCommandTask());
        invokeTaskLoop.setName("event-loop");
        invokeTaskLoop.start();
    }

    private GroupTaskWrapper createGroupTaskWrapper(String groupId) {
        return new GroupTaskWrapper(groupId, this.lock);
    }

    protected GroupTask createGroupTask(String groupId) {
        return new LinkListGroupTask<T>();
    }

    class LoopCommandTask implements Runnable {

        @Override
        public void run() {
            for (; ; ) {
                log.info("开始分发任务..");
                // 空的分组
                List<String> nullGroup = new ArrayList<>();
                GroupTaskWrapper groupTask;
                T t;
                long minDelay = Long.MAX_VALUE;
                long delay;
                for (Map.Entry<String, GroupTaskWrapper> entry : groupTaskMap.entrySet()) {
                    groupTask = entry.getValue();
                    log.info("处理群组{}的定时任务{}", entry.getKey(), groupTask);
                    if (!groupTask.hasTask()) {
                        log.info("群组{}没有更多任务准备移除", entry.getKey());
                        nullGroup.add(entry.getKey());
                    } else {
                        if ((delay = getFirstDelay(groupTask)) <= 0) {
                            log.info("群组：{}的首个任务到执行时间直接执行", entry.getKey());
                            if ((t = groupTask.getFirst()) != null) {
                                handleTask(entry.getKey(), t);
                                groupTask.removeFirst();
                            }
                        } else {
                            log.info("群组：{}的第一个任务还未到执行时间需要延迟：{}ms {}s, 任务为{}", entry.getKey(), delay, delay / 1000,
                                groupTask.getFirst());
                            minDelay = Math.min(minDelay, delay);
                        }
                    }
                }
                removeNullTaskFromMap(nullGroup);
                if (minDelay != Long.MAX_VALUE || groupTaskMap.size() == 0) {
                    synchronized (lock) {
                        log.info("剩余{}准备休眠{}", groupTaskMap.size(), minDelay);
                        if (working && minDelay == Long.MAX_VALUE) {
                            working = false;
                        }
                        try {
                            lock.wait(minDelay);
                            log.info("休眠时间结束");
                        } catch (InterruptedException e) {
                            log.info("休眠被打断开始,开始新的一轮循环");
                        }
                    }
                }
            }

        }

        private long getFirstDelay(GroupTask<T> task) {
            return getDelay(task.getFirst());
        }

        private void handleTask(String groupId, T t) {
            if (isAsyncTask(t)) {
                threadPoolExecutor.execute(() -> {
                    doTask(groupId, t);
                });
            } else {
                doTask(groupId, t);
            }
        }

        /**
         * 移除空的任务
         *
         * @param removeId
         */
        private void removeNullTaskFromMap(List<String> removeId) {
            for (String groupId : removeId) {
                groupTaskMap.remove(groupId);
            }
        }
    }


    class GroupTaskWrapper implements GroupTask<T> {

        String groupId;

        GroupTask<T> groupTask;

        GroupTaskListener listener;

        GroupTaskWrapper(String groupId, Object lock) {
            this.groupId = groupId;
            this.groupTask = createGroupTask(groupId);
            this.listener = new GroupTaskListener(groupTask, lock);
        }

        @Override
        public T getFirst() {
            return groupTask.getFirst();
        }

        @Override
        public void addTask(T t) {
            groupTask.addTask(t);
        }

        @Override
        public boolean hasTask() {
            return groupTask.hasTask();
        }

        @Override
        public T removeFirst() {
            T t = groupTask.removeFirst();
            if (groupTask.hasTask()) {
                onNextReady(groupTask.getFirst());
            }
            return t;
        }

        @Override
        public String toString() {
            return "wrapper{" +
                ", groupTask=" + groupTask +
                '}';
        }
    }

    protected void onNextReady(T t) {
    }


    protected ExecutorService getExecTaskService() {
        return null;
    }

    /**
     * 获取任务延迟
     *
     * @param first
     * @return
     */
    protected abstract long getDelay(T first);

    /**
     * 执行分组任务
     *
     * @param t
     */
    protected abstract void doTask(String groupId, final T t);

    /**
     * 是否是异步任务
     *
     * @param t
     * @return
     */
    protected abstract boolean isAsyncTask(T t);


}
