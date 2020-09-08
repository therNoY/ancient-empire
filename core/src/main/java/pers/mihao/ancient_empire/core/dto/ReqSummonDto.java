package pers.mihao.ancient_empire.core.dto;

import pers.mihao.ancient_empire.common.bo.Position;

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
