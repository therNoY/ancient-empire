package pers.mihao.ancient_empire.base.dto;

import pers.mihao.ancient_empire.common.dto.ApiRequestDTO;

import java.util.List;

/**
 * @version 1.0
 * @auther mihao
 * @date 2021\3\27 0027 15:40
 */
public class MapShowWithConfigDTO extends ApiRequestDTO {

    private String mapId;

    private List<ArmyConfig> armyConfigList;


    public String getMapId() {
        return mapId;
    }

    public void setMapId(String mapId) {
        this.mapId = mapId;
    }

    public List<ArmyConfig> getArmyConfigList() {
        return armyConfigList;
    }

    public void setArmyConfigList(List<ArmyConfig> armyConfigList) {
        this.armyConfigList = armyConfigList;
    }
}
