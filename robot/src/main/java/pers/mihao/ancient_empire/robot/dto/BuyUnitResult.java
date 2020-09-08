package pers.mihao.ancient_empire.robot.dto;

import pers.mihao.ancient_empire.common.bo.Site;
import pers.mihao.ancient_empire.common.bo.Unit;
import com.mihao.ancient_empire.entity.UnitMes;

public class BuyUnitResult extends ActiveResult{
    private Integer armyIndex;
    private UnitMes unitMes;
    private Unit unit;

    public BuyUnitResult(String id, Site site, UnitMes unitMes) {
        super.setRecordId(id);
        super.setSite(site);
        this.unitMes = unitMes;
    }

    public UnitMes getUnitMes() {
        return unitMes;
    }

    public void setUnitMes(UnitMes unitMes) {
        this.unitMes = unitMes;
    }

    public Integer getArmyIndex() {
        return armyIndex;
    }

    public void setArmyIndex(Integer armyIndex) {
        this.armyIndex = armyIndex;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }
}
