package pers.mihao.ancient_empire.base.bo;

import java.io.Serializable;
import java.util.List;

/**
 * 地图类 由长宽高 和基础方块组成
 */
public class GameMap implements Serializable {
    private static final long serialVersionUID = 8683452331192189L;
    // 宽
    private Integer row;
    // 高
    private Integer column;
    // 一个 n*m 的区域组成的地图
    List<Region> regions;

    public GameMap() {
    }

    public GameMap(Integer row, Integer column, List<Region> regions) {
        this.row = row;
        this.column = column;
        this.regions = regions;
    }

    public Integer getRow() {
        return row;
    }

    public void setRow(Integer row) {
        this.row = row;
    }

    public Integer getColumn() {
        return column;
    }

    public void setColumn(Integer column) {
        this.column = column;
    }

    public List<Region> getRegions() {
        return regions;
    }

    public void setRegions(List<Region> regions) {
        this.regions = regions;
    }
}
