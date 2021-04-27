package pers.mihao.ancient_empire.core.manger.interactive;

/**
 * 控制交互的锁
 * @Author mh32736
 * @Date 2021/4/27 14:56
 */
public class InteractiveLock {

    /**
     * 展示动画结束
     */
    private static final Object lock = new Object();

    /**
     * 有序命令执行中
     */
    public void executionIng() {
    }


    /**
     * 直到执行完
     */
    public void untilExecutionOk(int time){
        synchronized (lock) {
            try {
                lock.wait(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void untilExecutionOk(){
        // 最多等待10s
        untilExecutionOk(10 * 1000);
    }

    /**
     * 有序命令执行完毕
     */
    public void executionOK() {
        synchronized (lock) {
            lock.notifyAll();
        }
    }
}
