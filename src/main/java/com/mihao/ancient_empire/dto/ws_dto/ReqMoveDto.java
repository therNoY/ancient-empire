package com.mihao.ancient_empire.dto.ws_dto;

import com.mihao.ancient_empire.dto.Position;

import java.util.List;

public class ReqMoveDto {

    private Integer currentUnitIndex; // 当前单位的Index
    private Position currentPoint; // 当前点
    private Position aimPoint; // 目标点
    private List<Position> positions; // 移动区域

    public ReqMoveDto() {
    }

    public ReqMoveDto(Integer currentUnitIndex, Position currentPoint, Position aimPoint) {
        this.currentUnitIndex = currentUnitIndex;
        this.currentPoint = currentPoint;
        this.aimPoint = aimPoint;
    }

    public Integer getCurrentUnitIndex() {
        return currentUnitIndex;
    }

    public void setCurrentUnitIndex(Integer currentUnitIndex) {
        this.currentUnitIndex = currentUnitIndex;
    }

    public Position getCurrentPoint() {
        return currentPoint;
    }

    public void setCurrentPoint(Position currentPoint) {
        this.currentPoint = currentPoint;
    }

    public Position getAimPoint() {
        return aimPoint;
    }

    public void setAimPoint(Position aimPoint) {
        this.aimPoint = aimPoint;
    }

    public List<Position> getPositions() {
        return positions;
    }

    public void setPositions(List<Position> positions) {
        this.positions = positions;
    }
}
