package pers.mihao.ancient_empire.core.manger.event;

import java.util.Date;

/**
 * @Author mh32736
 * @Date 2020/9/10 13:24
 */
public abstract class AbstractEvent implements Event{

    protected String type;

    protected Date createTime;


    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
