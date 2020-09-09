package pers.mihao.ancient_empire.core.dto;

import pers.mihao.ancient_empire.base.bo.Position;

public class ReqAttachAreaDto {
    private Integer index;
    private Position position;

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }
}
