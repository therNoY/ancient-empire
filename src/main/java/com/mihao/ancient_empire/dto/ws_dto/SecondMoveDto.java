package com.mihao.ancient_empire.dto.ws_dto;

import com.mihao.ancient_empire.dto.Position;

import java.util.List;

public class SecondMoveDto {
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
