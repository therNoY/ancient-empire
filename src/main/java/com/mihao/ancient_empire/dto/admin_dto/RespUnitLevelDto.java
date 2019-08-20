package com.mihao.ancient_empire.dto.admin_dto;

import com.mihao.ancient_empire.entity.UnitLevelMes;

public class RespUnitLevelDto extends UnitLevelMes {
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


}
