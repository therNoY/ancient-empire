package pers.mihao.ancient_empire.base.dto;

import pers.mihao.ancient_empire.common.dto.ApiRequestDTO;

import java.util.List;

/**
 *
 * @version 1.0
 * @author mihao
 * @date 2021\1\1 0001 19:20
 */
public class TemplateIdDTO extends ApiRequestDTO {

    private String templateId;

    private List<String> filter;

    public List<String> getFilter() {
        return filter;
    }

    public void setFilter(List<String> filter) {
        this.filter = filter;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }
}
