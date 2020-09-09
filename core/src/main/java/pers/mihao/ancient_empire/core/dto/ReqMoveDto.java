package pers.mihao.ancient_empire.core.dto;

import java.util.List;
import pers.mihao.ancient_empire.base.bo.Position;

public class ReqMoveDto {

    private Integer currentUnitIndex; // 当前单位的Index
    private Position currentPoint; // 当前点
    private Position aimPoint; // 目标点
    private List<Position> moveArea; // 移动区域

    public ReqMoveDto() {
    }

    public ReqMoveDto(Position currentPoint, Position aimPoint, List<Position> moveArea) {
        this.currentPoint = currentPoint;
        this.aimPoint = aimPoint;
        this.moveArea = moveArea;
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

    public List<Position> getMoveArea() {
        return moveArea;
    }

    public void setMoveArea(List<Position> moveArea) {
        this.moveArea = moveArea;
    }
}
