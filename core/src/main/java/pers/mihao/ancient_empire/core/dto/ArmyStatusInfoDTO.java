package pers.mihao.ancient_empire.core.dto;

/**
 * 单位状态变换
 * @Author mh32736
 * @Date 2020/11/3 20:04
 */
public class ArmyStatusInfoDTO {

    /**
     * 军队Index
     */
    private Integer armyIndex;

    /**
     * 资金
     */
    private Integer money;


    public Integer getArmyIndex() {
        return armyIndex;
    }

    public void setArmyIndex(Integer armyIndex) {
        this.armyIndex = armyIndex;
    }

    public Integer getMoney() {
        return money;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }

}
