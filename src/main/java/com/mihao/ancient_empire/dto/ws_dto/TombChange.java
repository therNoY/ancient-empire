package com.mihao.ancient_empire.dto.ws_dto;

import com.mihao.ancient_empire.dto.Unit;

import java.util.List;

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
