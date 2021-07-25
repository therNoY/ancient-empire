package pers.mihao.ancient_empire.base.bo;

import java.util.List;

import pers.mihao.ancient_empire.base.entity.Ability;
import pers.mihao.ancient_empire.base.entity.UnitLevelMes;
import pers.mihao.ancient_empire.base.entity.UnitMes;

/**
 * 返回一个详细的单位 信息
 */
public class UnitInfo extends Unit {

    private static final long serialVersionUID = 1L;

    public UnitInfo() {
    }

    public UnitInfo(UnitMes unitMes, UnitLevelMes levelMes, List<Ability> abilities) {
        this.unitMes = unitMes;
        this.levelMes = levelMes;
        this.abilities = abilities;
        setType(unitMes.getType());
        setTypeId(unitMes.getId());
    }

    /**
     * 单位的信息
     */
    private UnitMes unitMes;
    /**
     * 单位的等级信息
     */
    private UnitLevelMes levelMes;
    /**
     * 单位的能力信息
     */
    private List<Ability> abilities;
    /**
     * 单位脚下的地形信息
     */
    private RegionInfo regionInfo;

    /**
     * 单位颜色
     */
    private String color;

    private Integer armyIndex;

    private Integer unitIndex;

    public Integer getArmyIndex() {
        return armyIndex;
    }

    public void setArmyIndex(Integer armyIndex) {
        this.armyIndex = armyIndex;
    }

    public Integer getUnitIndex() {
        return unitIndex;
    }

    public void setUnitIndex(Integer unitIndex) {
        this.unitIndex = unitIndex;
    }

    public UnitMes getUnitMes() {
        return unitMes;
    }

    public void setUnitMes(UnitMes unitMes) {
        this.unitMes = unitMes;
    }

    public UnitLevelMes getLevelMes() {
        return levelMes;
    }

    public void setLevelMes(UnitLevelMes levelMes) {
        this.levelMes = levelMes;
    }

    public List<Ability> getAbilities() {
        return abilities;
    }

    public void setAbilities(List<Ability> abilities) {
        this.abilities = abilities;
    }

    public RegionInfo getRegionInfo() {
        return regionInfo;
    }

    public void setRegionInfo(RegionInfo regionInfo) {
        this.regionInfo = regionInfo;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return simpleInfoShow();
    }

    public String simpleInfoShow() {
        return unitMes.getName() + "-" + levelMes.getLevel() + "(" + getRow() + "," + getColumn() + ");";
    }

    public String detailInfoShow() {
        return "[地图信息:" + super.toString() + "]";
    }
}
