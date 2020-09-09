package pers.mihao.ancient_empire.core.dto;

import java.io.Serializable;
import java.util.List;
import pers.mihao.ancient_empire.base.bo.BaseSquare;

public class RespRepairOcpResult implements Serializable {

    private Integer regionIndex; /* 要占领的地形index */
    private BaseSquare square; /* 要占领的地形 */
    private SecondMoveDto secondMove; /* 二次移动区域 */
    private String recordId; /* 地图Id */
    private List<PathPosition> pathPositions; /* 移动路径 */

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

    public List<PathPosition> getPathPositions() {
        return pathPositions;
    }

    public void setPathPositions(List<PathPosition> pathPositions) {
        this.pathPositions = pathPositions;
    }
}