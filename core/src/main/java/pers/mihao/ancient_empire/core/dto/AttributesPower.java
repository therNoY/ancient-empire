package pers.mihao.ancient_empire.core.dto;

/**
 * 单位的攻击力 和结果加成
 */
public class AttributesPower {
    /**
     * 基础攻击力/防御力
     */
    public Integer num;
    /**
     * 攻击/防御 加成
     */
    public Float addition;

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
