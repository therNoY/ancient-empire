package com.mihao.ancient_empire.dto.map_dto;

import com.mihao.ancient_empire.dto.Site;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class ReqBuyUnitDto {

    @NotBlank
    private String uuid;
    @DecimalMin(value = "-1")
    private Integer unitId;
    @NotNull
    private Site site;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getUnitId() {
        return unitId;
    }

    public void setUnitId(Integer unitId) {
        this.unitId = unitId;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }
}
