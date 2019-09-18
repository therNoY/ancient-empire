package com.mihao.ancient_empire.dto.ws_dto;

import com.mihao.ancient_empire.dto.Position;

public class ReqSummonDto extends ReqSecondMoveDto{
    private Integer index; // 单位的Index
    private Position tomb; // 召唤坟墓的位置

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
}
