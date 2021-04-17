package pers.mihao.ancient_empire.base.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import pers.mihao.ancient_empire.common.dto.ApiRequestDTO;

public class ReqSaveRecordDTO extends ApiRequestDTO {
    @NotEmpty(message = "地图的Id 不能是空")
    private String uuid;
    @NotBlank(message = "地图名称不能为空")
    private String name;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
