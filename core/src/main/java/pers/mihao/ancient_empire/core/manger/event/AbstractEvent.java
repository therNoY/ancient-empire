package pers.mihao.ancient_empire.core.manger.event;

import java.util.Date;

/**
 * 抽象事件
 * @Author mh32736
 * @Date 2020/9/10 13:24
 */
public abstract class AbstractEvent implements Event{

    protected Date createTime;


    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

}
