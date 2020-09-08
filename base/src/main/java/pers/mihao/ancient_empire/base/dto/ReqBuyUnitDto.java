package pers.mihao.ancient_empire.base.dto;

import pers.mihao.ancient_empire.base.bo.Site;

public class ReqBuyUnitDto {

    private String type;
    private Site site;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }
}
