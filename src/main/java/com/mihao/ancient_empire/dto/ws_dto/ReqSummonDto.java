package com.mihao.ancient_empire.dto.ws_dto;

import com.mihao.ancient_empire.dto.Position;

import java.util.List;

public class ReqSummonDto {
    private Integer index;
    private Position tomb;
    private List<PathPosition> path;

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Position getTomb() {
        return tomb;
    }

    public void setTomb(Position tomb) {
        this.tomb = tomb;
    }

    public List<PathPosition> getPath() {
        return path;
    }

    public void setPath(List<PathPosition> path) {
        this.path = path;
    }
}
