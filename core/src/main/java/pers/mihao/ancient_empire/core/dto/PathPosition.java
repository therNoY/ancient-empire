package pers.mihao.ancient_empire.core.dto;

import pers.mihao.ancient_empire.base.bo.Position;
import pers.mihao.ancient_empire.base.bo.Site;

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
