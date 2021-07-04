package pers.mihao.ancient_empire.base.dto;

import pers.mihao.ancient_empire.common.dto.ApiRequestDTO;

/**
 * @Author mihao
 * @Date 2021/5/9 18:24
 */
public class UserMapIdDTO extends ApiRequestDTO {

    private String mapId;

    public String getMapId() {
        return mapId;
    }

    public void setMapId(String mapId) {
        this.mapId = mapId;
    }
}
