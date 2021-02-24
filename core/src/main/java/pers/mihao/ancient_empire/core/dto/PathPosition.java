package pers.mihao.ancient_empire.core.dto;

import pers.mihao.ancient_empire.base.bo.Position;
import pers.mihao.ancient_empire.base.bo.Site;

/**
 * 记录单位行动的路径
 */
public class PathPosition extends Site{


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


}
