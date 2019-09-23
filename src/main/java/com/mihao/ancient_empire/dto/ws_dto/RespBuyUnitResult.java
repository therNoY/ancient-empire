package com.mihao.ancient_empire.dto.ws_dto;

import com.mihao.ancient_empire.dto.Position;
import com.mihao.ancient_empire.dto.mongo_dto.BuyUnitDto;

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
