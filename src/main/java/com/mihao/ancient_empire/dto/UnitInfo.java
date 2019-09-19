package com.mihao.ancient_empire.dto;

import com.mihao.ancient_empire.entity.Ability;
import com.mihao.ancient_empire.entity.UnitLevelMes;
import com.mihao.ancient_empire.entity.UnitMes;

import java.io.Serializable;
import java.util.List;

/**
 * 返回一个详细的单位 信息
 */
public class UnitInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    public UnitInfo() {
    }

    public UnitInfo(UnitMes unit, UnitLevelMes level, List<Ability> abilities) {
        this.unit = unit;
        this.level = level;
        this.abilities = abilities;
    }

    private UnitMes unit;
    private UnitLevelMes level;
    private List<Ability> abilities;

    public UnitMes getUnit() {
        return unit;
    }

    public void setUnit(UnitMes unit) {
        this.unit = unit;
    }

    public UnitLevelMes getLevel() {
        return level;
    }

    public void setLevel(UnitLevelMes level) {
        this.level = level;
    }

    public List<Ability> getAbilities() {
        return abilities;
    }

    public void setAbilities(List<Ability> abilities) {
        this.abilities = abilities;
    }
}