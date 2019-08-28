package com.mihao.ancient_empire.dto.ws_dto;

import com.mihao.ancient_empire.dto.Position;

import java.util.List;

public class ReqMoveDto {

    private Position currentPoint;
    private Position aimPoint;
    private List<Position> positions;

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
