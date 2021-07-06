package pers.mihao.ancient_empire.core.manger.event;

import java.util.Date;
import pers.mihao.ancient_empire.auth.entity.User;
import pers.mihao.ancient_empire.common.enums.LanguageEnum;

/**
 * 事件类
 * @Author mihao
 * @Date 2020/9/10 13:30
 */
public interface Event {

    /**
     * 事件触发的用户
     * @param user
     */
    void setUser(User user);

    /**
     * 事件类型的ID
     * @param id
     */
    void setId(String id);

    /**
     * 事件的创建时间
     * @param createTime
     */
    void setCreateTime(Date createTime);

    /**
     * 国际化
     * @param languageEnum
     * @return
     */
    void setLanguage(LanguageEnum languageEnum);

}
