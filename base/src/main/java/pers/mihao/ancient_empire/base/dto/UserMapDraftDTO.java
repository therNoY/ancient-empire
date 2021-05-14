package pers.mihao.ancient_empire.base.dto;

import pers.mihao.ancient_empire.common.dto.ApiRequestDTO;

/**
 * 用户草稿地图DTO
 * @Author mh32736
 * @Date 2021/4/10 17:58
 */
public class UserMapDraftDTO extends ApiRequestDTO {

    private Integer templateId;

    private String mapName;

    private Integer mapRow;

    private Integer mapColumn;

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public Integer getMapRow() {
        return mapRow;
    }

    public void setMapRow(Integer mapRow) {
        this.mapRow = mapRow;
    }

    public Integer getMapColumn() {
        return mapColumn;
    }

    public void setMapColumn(Integer mapColumn) {
        this.mapColumn = mapColumn;
    }

    public Integer getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Integer templateId) {
        this.templateId = templateId;
    }

    @Override
    public String toString() {
        return "UserMapDraftDTO{" +
            "templateId=" + templateId +
            ", mapName='" + mapName + '\'' +
            ", mapRow=" + mapRow +
            ", mapColumn=" + mapColumn +
            '}';
    }
}
