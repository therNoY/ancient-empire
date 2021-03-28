package pers.mihao.ancient_empire.base.dto;

import pers.mihao.ancient_empire.common.dto.ApiRequestDTO;

/**
 * @version 1.0
 * @auther mihao
 * @date 2021\3\27 0027 23:07
 */
public class RoomArmyChangeDTO extends ApiRequestDTO {

    private String newArmy;

    public String getNewArmy() {
        return newArmy;
    }

    public void setNewArmy(String newArmy) {
        this.newArmy = newArmy;
    }
}
