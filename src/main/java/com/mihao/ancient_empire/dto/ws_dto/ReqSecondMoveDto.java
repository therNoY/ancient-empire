package com.mihao.ancient_empire.dto.ws_dto;

import java.io.Serializable;
import java.util.List;

public class ReqSecondMoveDto implements Serializable {

    private List<PathPosition> path; // 单位移动的地点

    public List<PathPosition> getPath() {
        return path;
    }

    public void setPath(List<PathPosition> path) {
        this.path = path;
    }
}
