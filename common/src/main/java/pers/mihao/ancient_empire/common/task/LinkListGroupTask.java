package pers.mihao.ancient_empire.common.task;

import java.util.Deque;
import java.util.LinkedList;

/**
 * 默认没有延迟的组队列
 *
 * @Author mihao
 * @Date 2021/7/19 19:27
 */
public class LinkListGroupTask<T extends GroupCommend> implements GroupTask<T> {

    private Deque<T> deque = new LinkedList<>();

    @Override
    public T getFirst() {
        return deque.getFirst();
    }

    protected int getDelay(T t) {
        return 0;
    }

    @Override
    public void addTask(T t) {
        deque.addLast(t);
    }

    @Override
    public boolean hasTask() {
        return deque.size() > 0;
    }

    @Override
    public T removeFirst() {
        return deque.removeFirst();
    }
}
