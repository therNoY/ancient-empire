package pers.mihao.ancient_empire.common.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author mihao
 * @Date 2021/7/19 22:20
 */
public class GroupTaskListener implements Listener<String> {

    Logger log = LoggerFactory.getLogger(GroupTaskListener.class);

    Object lock;

    GroupTask task;

    public GroupTaskListener(GroupTask<? extends GroupCommend> groupTask, Object lock) {
        this.lock = lock;
        this.task = groupTask;
    }

    @Override
    public void notifyEvent(String id) {
        if (task.hasTask()) {
            GroupCommend groupCommend = task.getFirst();
            groupCommend.notifyFirst();
            log.info("动画执行完毕准备执行新的命令", groupCommend);
        }
        synchronized (lock) {
            lock.notifyAll();
        }
    }
}
