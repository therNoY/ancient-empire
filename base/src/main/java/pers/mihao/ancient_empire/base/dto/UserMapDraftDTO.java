package pers.mihao.ancient_empire.base.dto;

import pers.mihao.ancient_empire.common.dto.ApiRequestDTO;

/**
 * 用户草噶地图DTO
 * @Author mh32736
 * @Date 2021/4/10 17:58
 */
public class UserMapDraftDTO extends ApiRequestDTO {

    private Integer templateId;

    public Integer getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Integer templateId) {
        this.templateId = templateId;
    }
}
