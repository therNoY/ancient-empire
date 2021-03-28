package pers.mihao.ancient_empire.base.dto;

import pers.mihao.ancient_empire.common.dto.ApiRequestDTO;

/**
 * @version 1.0
 * @auther mihao
 * @date 2021\3\27 0027 22:40
 */
public class ChangeRoomDTO extends ApiRequestDTO {

    private String changeRoom;

    public String getChangeRoom() {
        return changeRoom;
    }

    public void setChangeRoom(String changeRoom) {
        this.changeRoom = changeRoom;
    }
}
