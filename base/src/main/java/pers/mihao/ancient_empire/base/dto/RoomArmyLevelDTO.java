package pers.mihao.ancient_empire.base.dto;

import pers.mihao.ancient_empire.common.dto.ApiRequestDTO;

/**
 * @version 1.0
 * @auther mihao
 * @date 2021\3\29 0029 22:41
 */
public class RoomArmyLevelDTO extends ApiRequestDTO {

    private String color;

    private Integer playerId;

    private String roomId;

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public Integer getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Integer playerId) {
        this.playerId = playerId;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
