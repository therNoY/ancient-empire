package pers.mihao.ancient_empire.core.manger.event;

import pers.mihao.ancient_empire.auth.entity.User;

import java.util.Date;
import pers.mihao.ancient_empire.common.enums.LanguageEnum;

/**
 * 抽象事件
 * @Author mihao
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

    private LanguageEnum language;

    protected Date createTime;

    public User getUser() {
        return user;
    }

    @Override
    public void setUser(User user) {
        this.user = user;
    }

    public Date getCreateTime() {
        return createTime;
    }

    @Override
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public LanguageEnum getLanguage() {
        return language;
    }

    @Override
    public void setLanguage(LanguageEnum language) {
        this.language = language;
    }
}
