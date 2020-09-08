package pers.mihao.ancient_empire.base.bo;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;

public class ReqUnitInfoDto {
    @NotBlank(message = "单位类型不能为空")
    private String type;
    @DecimalMin(value = "0", message = "单位等级错误")
    private Integer level;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }
}
