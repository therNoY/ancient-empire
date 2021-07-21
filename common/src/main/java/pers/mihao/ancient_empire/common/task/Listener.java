package pers.mihao.ancient_empire.common.task;

/**
 * @Author mihao
 * @Date 2021/7/19 22:12
 */
public interface Listener<E> {

    /**
     * 通知事件产生
     *
     * @param e
     */
    void notifyEvent(E e);

}
