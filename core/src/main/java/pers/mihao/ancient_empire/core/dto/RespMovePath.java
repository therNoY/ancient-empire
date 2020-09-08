package pers.mihao.ancient_empire.core.dto;

import java.util.List;

public class RespMovePath {
    private List<PathPosition> pathPositions;
    private Integer lastSpeed;

    public List<PathPosition> getPathPositions() {
        return pathPositions;
    }

    public void setPathPositions(List<PathPosition> pathPositions) {
        this.pathPositions = pathPositions;
    }

    public Integer getLastSpeed() {
        return lastSpeed;
    }

    public void setLastSpeed(Integer lastSpeed) {
        this.lastSpeed = lastSpeed;
    }
}
