package pers.mihao.ancient_empire.core.manger.event;

import pers.mihao.ancient_empire.auth.entity.User;

import java.util.Date;

/**
 * 抽象事件
 * @Author mh32736
 * @Date 2020/9/10 13:24
 */
public abstract class AbstractEvent implements Event{

    /**
     * 相关ID
     */
    private String id;

    /**
     * 登录人ID
     */
    private User user;

    protected Date createTime;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
