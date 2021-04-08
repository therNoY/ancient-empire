package pers.mihao.ancient_empire.base.bo;

import java.io.Serializable;
import java.util.Objects;

/**
 * 坐标信息
 */
public class Site implements Serializable {

    /**
     * 行
     */
    protected Integer row;
    /**
     * 列
     */
    protected Integer column;


    public Site() {
    }

    public Site(Integer row, Integer column) {
        this.row = row;
        this.column = column;
    }

    public Site(Site site) {
        this.row = site.getRow();
        this.column = site.getColumn();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Site) {
            Site that = (Site) obj;
            return row.equals(that.getRow()) && column.equals(that.getColumn());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }

    @Override
    public String toString() {
        return "row=" + row + ", column=" + column;

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
}
