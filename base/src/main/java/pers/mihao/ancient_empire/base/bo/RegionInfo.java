package pers.mihao.ancient_empire.base.bo;

import pers.mihao.ancient_empire.base.entity.RegionMes;

/**
 * 游戏地形信息
 * @version 1.0
 * @author mihao
 * @date 2020\10\3 0003 8:17
 */
public class RegionInfo extends RegionMes {

    private String color;

    /**
     * 行
     */
    protected Integer row;
    /**
     * 列
     */
    protected Integer column;

    private Integer index;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
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

    public Site getSite(){
        return new Site(row, column);
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }
}
