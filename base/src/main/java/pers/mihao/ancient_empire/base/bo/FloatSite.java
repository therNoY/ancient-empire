package pers.mihao.ancient_empire.base.bo;

import java.io.Serializable;

/**
 * @version 1.0
 * @auther mihao
 * @date 2020\10\12 0012 21:52
 */
public class FloatSite implements Serializable {
    private Double row;
    private Double column;

    public FloatSite(Double row, Double column) {
        this.row = row;
        this.column = column;
    }

    public Double getRow() {
        return row;
    }

    public void setRow(Double row) {
        this.row = row;
    }

    public Double getColumn() {
        return column;
    }

    public void setColumn(Double column) {
        this.column = column;
    }
}
