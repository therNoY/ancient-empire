package com.mihao.ancient_empire.dto.ws_dto;

import com.mihao.ancient_empire.dto.Unit;

import java.util.List;

/**
 * 客户端 查询攻击结果需要的dto
 */
public class ReqAttachDto {

    private Unit attachUnit;
    private Unit beAttachUnit;
    private List<PathPosition> path;

    public Unit getAttachUnit() {
        return attachUnit;
    }

    public void setAttachUnit(Unit attachUnit) {
        this.attachUnit = attachUnit;
    }

    public Unit getBeAttachUnit() {
        return beAttachUnit;
    }

    public void setBeAttachUnit(Unit beAttachUnit) {
        this.beAttachUnit = beAttachUnit;
    }

    public List<PathPosition> getPath() {
        return path;
    }

    public void setPath(List<PathPosition> path) {
        this.path = path;
    }
}
