package pers.mihao.ancient_empire.base.dto;

import javax.validation.constraints.NotNull;
import pers.mihao.ancient_empire.common.dto.ApiRequestDTO;

/**
 * @Author mihao
 * @Date 2021/3/11 9:39
 */
public class ReqRoomIdDTO extends ApiRequestDTO {

    @NotNull
    private String roomId;

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
}
