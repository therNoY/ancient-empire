package com.mihao.ancient_empire.dto.map_dto;

import com.mihao.ancient_empire.dto.Army;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;
import java.util.List;

public class ReqInitMapDto {
    @DecimalMin(value = "5", message = "最小人口大于5")
    private Integer maxPop;
    @DecimalMin(value = "500", message = "初始金额要大于500")
    private Integer money;
    @NotEmpty(message = "地图Id 不能为空")
    private String uuid;
    @NotEmpty(message = "军队不能为空")
    List<Army> armyList;

    public Integer getMaxPop() {
        return maxPop;
    }

    public void setMaxPop(Integer maxPop) {
        this.maxPop = maxPop;
    }

    public String getUuid() {
        return uuid;
    }

    public Integer getMoney() {
        return money;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public List<Army> getArmyList() {
        return armyList;
    }

    public void setArmyList(List<Army> armyList) {
        this.armyList = armyList;
    }
}
