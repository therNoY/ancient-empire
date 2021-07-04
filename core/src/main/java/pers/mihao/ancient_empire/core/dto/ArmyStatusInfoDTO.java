package pers.mihao.ancient_empire.core.dto;

/**
 * 单位状态变换
 *
 * @Author mihao
 * @Date 2020/11/3 20:04
 */
public class ArmyStatusInfoDTO {

    private String color;
    /**
     * 资金
     */
    private Integer money;

    /**
     * 人口
     */
    private Integer pop;

    public Integer getMoney() {
        return money;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }

    public Integer getPop() {
        return pop;
    }

    public void setPop(Integer pop) {
        this.pop = pop;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "ArmyStatusInfoDTO{" +
            "color='" + color + '\'' +
            ", money=" + money +
            ", pop=" + pop +
            '}';
    }
}
