package com.mihao.ancient_empire.ai.dto;

import com.mihao.ancient_empire.dto.BaseSquare;
import com.mihao.ancient_empire.dto.Site;

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
