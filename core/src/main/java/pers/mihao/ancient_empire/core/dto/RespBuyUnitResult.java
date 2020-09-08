package pers.mihao.ancient_empire.core.dto;

import pers.mihao.ancient_empire.common.bo.Position;

import java.util.List;

public class RespBuyUnitResult {

    private BuyUnitDto buyUnitDto;

    private List<Position> moveArea; // 移动范围

    public BuyUnitDto getBuyUnitDto() {
        return buyUnitDto;
    }

    public void setBuyUnitDto(BuyUnitDto buyUnitDto) {
        this.buyUnitDto = buyUnitDto;
    }

    public List<Position> getMoveArea() {
        return moveArea;
    }

    public void setMoveArea(List<Position> moveArea) {
        this.moveArea = moveArea;
    }
}
