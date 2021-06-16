package pers.mihao.ancient_empire.base.enums;

import pers.mihao.ancient_empire.common.enums.BaseEnum;

/**
 * 军队颜色枚举 固定
 * @author mihao
 */
public enum ColorEnum implements BaseEnum {
    /**
     * 蓝色
     */
    BLUE("蓝色"),
    /**
     * 红色
     */
    RED("红色"),
    /**
     * 绿色
     */
    GREEN("绿色"),
    /**
     * 黑色
     */
    BLACK("黑色");

    String zhString;

    ColorEnum(String zhString) {
        this.zhString = zhString;
    }

    public String getZhString() {
        return zhString;
    }
}
