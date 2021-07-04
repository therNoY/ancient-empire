package pers.mihao.ancient_empire.base.dto;

import pers.mihao.ancient_empire.common.dto.ApiRequestDTO;

/**
 * @Author mihao
 * @Date 2021/4/29 23:21
 */
public class ReqUnitIdDTO extends ApiRequestDTO {

    private Integer unitId;

    public Integer getUnitId() {
        return unitId;
    }

    public void setUnitId(Integer unitId) {
        this.unitId = unitId;
    }
}
