package com.mihao.ancient_empire.dto.ws_dto;

/**
 * 用于确定单位的攻击力
 */
public class AttributesPower {
    public Integer num; // 基础攻击力/防御力
    public Float addition; // 加成

    public AttributesPower() {
    }

    public AttributesPower(Integer num) {
        this.num = num;
    }

    public AttributesPower(Integer num, Float addition) {
        this.num = num;
        this.addition = addition;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Float getAddition() {
        return addition;
    }

    public void setAddition(Float addition) {
        this.addition = addition;
    }

    public int attachPower() {
        return (int) (num * addition);
    }
}
