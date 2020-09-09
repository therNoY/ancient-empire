package pers.mihao.ancient_empire.core.dto;

import java.util.List;
import pers.mihao.ancient_empire.base.bo.Unit;

/**
 * 坟墓改变
 */
public class TombChange {

    List<Unit> unitList;

    List<Integer> tombs;

    public List<Unit> getUnitList() {
        return unitList;
    }

    public void setUnitList(List<Unit> unitList) {
        this.unitList = unitList;
    }

    public List<Integer> getTombs() {
        return tombs;
    }

    public void setTombs(List<Integer> tombs) {
        this.tombs = tombs;
    }
}
