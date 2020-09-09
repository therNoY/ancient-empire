package pers.mihao.ancient_empire.core.dto;

import java.io.Serializable;
import java.util.List;
import pers.mihao.ancient_empire.base.bo.Position;

public class SecondMoveDto implements Serializable {
    private Boolean secondMove; // 单位是否有二次移动
    private List<Position> moveArea; // 单位二次移动的移动范围


    public Boolean getSecondMove() {
        return secondMove;
    }

    public void setSecondMove(Boolean secondMove) {
        this.secondMove = secondMove;
    }

    public List<Position> getMoveArea() {
        return moveArea;
    }

    public void setMoveArea(List<Position> moveArea) {
        this.moveArea = moveArea;
    }
}
