package pers.mihao.ancient_empire.common.bean;

import pers.mihao.ancient_empire.common.enums.LanguageEnum;

/**
 * @Author mihao
 * @Date 2021/5/15 9:31
 */
public class UserLanguageSet {

    private static ThreadLocal<LanguageEnum> language = new ThreadLocal<>();

    public static LanguageEnum getLanguage() {
        return language.get();
    }

    public static void setLanguage(LanguageEnum userId){
        language.set(userId);
    }

    public static void clean(){
        language.remove();
    }

}
