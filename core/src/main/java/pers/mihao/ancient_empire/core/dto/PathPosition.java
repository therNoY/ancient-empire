package pers.mihao.ancient_empire.core.dto;

import pers.mihao.ancient_empire.base.bo.Position;
import pers.mihao.ancient_empire.base.bo.Site;

/**
 * 记录单位行动的路径 只记录转折点
 */
public class PathPosition extends Site{

    /**
     * 记录两个点之间的距离 方便前台单位展示移动动画
     */
    private Integer length;

    public PathPosition() {
    }

    public PathPosition(Position position) {
        this.row = position.getRow();
        this.column = position.getColumn();
    }

    public PathPosition(Site position) {
        this.row = position.getRow();
        this.column = position.getColumn();
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }
}
