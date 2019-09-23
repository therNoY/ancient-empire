package com.mihao.ancient_empire.dto.map_dto;

import com.mihao.ancient_empire.dto.Site;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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
