package com.mihao.ancient_empire.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Position {
    private Integer row;
    private Integer column;
    @JsonIgnore
    private Integer lastMove; // 剩余的移动力
    @JsonIgnore
    private int direction;

    public Position() {
    }

    public Position(Position p) {
        this.row = p.getRow();
        this.column = p.getColumn();
    }


    public Position(Integer row, Integer column) {
        this.row = row;
        this.column = column;
    }

    public Position(Integer row, Integer column, Integer lastMove, int direction) {
        this.row = row;
        this.column = column;
        this.lastMove = lastMove;
        this.direction = direction;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Position) {
            Position p1 = (Position) obj;
            if (this.row == p1.getRow() && this.column == p1.getColumn()) {
                // 如果当前的剩剩余移动力比较小就不算了
                if (p1.getLastMove() != null && this.lastMove != null) {
                    if ( p1.getLastMove() > this.lastMove) {
                        return true;
                    }
                }else {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 100 * this.row + this.column;
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

    public Integer getLastMove() {
        return lastMove;
    }

    public void setLastMove(Integer lastMove) {
        this.lastMove = lastMove;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }
}
