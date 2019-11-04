package com.mihao.ancient_empire.ai.dto;

import com.mihao.ancient_empire.dto.Site;
import com.mihao.ancient_empire.entity.UnitMes;

public class BuyUnitResult extends ActiveResult{

    Site site;
    UnitMes unitMes;


    public BuyUnitResult(String id, Site site, UnitMes unitMes) {
        super.setRecordId(id);
        this.site = site;
        this.unitMes = unitMes;
    }

    @Override
    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public UnitMes getUnitMes() {
        return unitMes;
    }

    public void setUnitMes(UnitMes unitMes) {
        this.unitMes = unitMes;
    }
}
