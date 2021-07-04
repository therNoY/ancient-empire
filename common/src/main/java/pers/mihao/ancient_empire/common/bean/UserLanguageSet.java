package pers.mihao.ancient_empire.common.bean;

import pers.mihao.ancient_empire.common.enums.LanguageEnum;

/**
 * @Author mihao
 * @Date 2021/5/15 9:31
 */
public interface UserLanguageSet {

    ThreadLocal<LanguageEnum> LANG = new ThreadLocal<>();

    /**
     * 获取当前用户的语言设置
     *
     * @param userId
     * @return
     */
    LanguageEnum getLanguageByUserId(String userId);

}
