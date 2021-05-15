package pers.mihao.ancient_empire.common.enums;

/**
 * @Author mh32736
 * @Date 2021/5/15 9:42
 */
public enum LanguageEnum implements BaseEnum {

    /**
     * 简体中文
     */
    LANG_ZH("zh"),

    /**
     * 英文
     */
    LANG_EN("en");

    String key;

    LanguageEnum(String key) {
        this.key = key;
    }


    @Override
    public String type() {
        return this.key;
    }
}
