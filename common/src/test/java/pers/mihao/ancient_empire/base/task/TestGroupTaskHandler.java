package pers.mihao.ancient_empire.base.task;

import pers.mihao.ancient_empire.base.task.TestGroupTaskHandler.TestInteger;
import pers.mihao.ancient_empire.common.task.AbstractGroupTaskHandler;
import pers.mihao.ancient_empire.common.task.GroupCommend;

/**
 * @Author mihao
 * @Date 2021/7/19 19:34
 */
public class TestGroupTaskHandler extends AbstractGroupTaskHandler<TestInteger> {

    @Override
    protected long getDelay(TestInteger first) {
        return (first.delayTime - System.currentTimeMillis());
    }

    @Override
    protected void doTask(String groupId, TestInteger testInteger) {
        System.out.println("组" + groupId + "执行延迟" + testInteger.delay + "的任务");
    }

    @Override
    protected boolean isAsyncTask(TestInteger testInteger) {
        return true;
    }

    static class TestInteger implements GroupCommend {

        volatile Integer delay;

        Long delayTime;

        public TestInteger(Integer delay) {
            this.delay = delay;
            this.delayTime = System.currentTimeMillis() + delay * 1000;
        }

        public Integer getDelay() {
            return delay;
        }

        public void setDelay(Integer delay) {
            this.delay = delay;
        }

        public Long getDelayTime() {
            return delayTime;
        }

        public void setDelayTime(Long delayTime) {
            this.delayTime = delayTime;
        }

        @Override
        public void notifyFirst() {
            this.delay = 0;
        }
    }
}
