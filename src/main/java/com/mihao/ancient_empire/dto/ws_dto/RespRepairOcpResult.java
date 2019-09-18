package com.mihao.ancient_empire.dto.ws_dto;

import com.mihao.ancient_empire.dto.BaseSquare;

import java.io.Serializable;

public class RespRepairOcpResult implements Serializable {

    private Integer regionIndex;
    private BaseSquare square;
    private SecondMoveDto secondMove;
    private String recordId;

    public SecondMoveDto getSecondMove() {
        return secondMove;
    }

    public void setSecondMove(SecondMoveDto secondMove) {
        this.secondMove = secondMove;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public Integer getRegionIndex() {
        return regionIndex;
    }

    public void setRegionIndex(Integer regionIndex) {
        this.regionIndex = regionIndex;
    }

    public BaseSquare getSquare() {
        return square;
    }

    public void setSquare(BaseSquare square) {
        this.square = square;
    }

}
