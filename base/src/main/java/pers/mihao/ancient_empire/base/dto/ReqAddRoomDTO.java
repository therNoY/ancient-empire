package pers.mihao.ancient_empire.base.dto;

import pers.mihao.ancient_empire.common.dto.ApiRequestDTO;

/**
 * 请求创建房间
 * @Author mh32736
 * @Date 2021/3/3 9:33
 */
public class ReqAddRoomDTO extends ApiRequestDTO {

    /**
     * 房间名字
     */
    private String roomName;


    /**
     * { key: "1", value: "公开" },
     * { key: "2", value: "私有" },
     */
    private String gameType;

    /**
     * 选择的地图id
     */
    private InitMapDTO initMap;

    /**
     * 玩家数量
     */
    private int playerCount;


    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public InitMapDTO getInitMap() {
        return initMap;
    }

    public void setInitMap(InitMapDTO initMap) {
        this.initMap = initMap;
    }

    public String getGameType() {
        return gameType;
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public void setPlayerCount(int playerCount) {
        this.playerCount = playerCount;
    }
}
