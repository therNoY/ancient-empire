package pers.mihao.ancient_empire.core.dto;

/**
 * 单位状态变换
 * @Author mh32736
 * @Date 2020/11/3 20:04
 */
public class ArmyStatusInfoDTO {

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
}
