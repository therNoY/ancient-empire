package pers.mihao.ancient_empire.robot.dto;

import pers.mihao.ancient_empire.common.bo.BaseSquare;
import pers.mihao.ancient_empire.common.bo.Site;

public class CastleRegion extends BaseSquare {


    public CastleRegion(BaseSquare baseSquare, Site site) {
        super(baseSquare.getColor(), baseSquare.getType());
        this.site = site;
    }


    private Site site;

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    @Override
    public String toString() {
        return site.toString();
    }
}
