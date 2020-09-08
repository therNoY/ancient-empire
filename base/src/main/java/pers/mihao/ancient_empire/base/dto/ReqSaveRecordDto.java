package pers.mihao.ancient_empire.base.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

public class ReqSaveRecordDto {
    @NotEmpty(message = "地图的Id 不能是空")
    private String uuid; // 地图的Id
    @NotBlank(message = "地图名称不能为空")
    private String name; // 地图的name

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
