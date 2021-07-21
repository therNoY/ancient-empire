package pers.mihao.ancient_empire.core.manger.interactive;

import pers.mihao.ancient_empire.common.task.Listener;
import pers.mihao.ancient_empire.core.manger.GameContext;

/**
 * 控制交互的锁 主要为了方便人机可以再一个动画执行完毕之后执行下一个动作
 *
 * @Author mihao
 * @Date 2021/4/27 14:56
 */
public class InteractiveLock {

    private GameContext gameContext;
    /**
     * 展示动画结束
     */
    private static final Object lock = new Object();

    private Boolean isExecutionIng = false;

    private Listener listener;

    public InteractiveLock(GameContext gameContext) {
        this.gameContext = gameContext;
    }

    /**
     * 有序命令执行中
     */
    public void executionIng() {
        synchronized (lock) {
            isExecutionIng = true;
        }
    }

    /**
     * 当前是否有执行的动画
     *
     * @return
     */
    public boolean isExecutionIng() {
        synchronized (lock) {
            return isExecutionIng;
        }
    }

    /**
     * 等待前端交互完成 再等待一段时间
     *
     * @param time
     * @param awaitTime
     */
    public void untilExecutionOk(int time, int awaitTime) {
        untilExecutionOk(time);
        try {
            Thread.sleep(awaitTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 直到执行完
     */
    public void untilExecutionOk(int time) {
        synchronized (lock) {
            try {
                lock.wait(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 有序命令执行完毕
     */
    public void executionOK() {
        synchronized (lock) {
            this.isExecutionIng = false;
            if (listener != null) {
                listener.notifyEvent(gameContext.getGameId());
            }
            lock.notifyAll();
        }
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public Listener getListener() {
        return listener;
    }
}
