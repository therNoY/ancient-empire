package pers.mihao.ancient_empire.base.bo;

/**
 * 基础的单位元素
 */
public class BaseUnit extends BaseSquare {

    private Integer row;
    private Integer column;

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
