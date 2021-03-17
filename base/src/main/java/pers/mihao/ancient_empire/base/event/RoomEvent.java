package pers.mihao.ancient_empire.base.event;

import org.springframework.context.ApplicationEvent;

/**
 * @Author mh32736
 * @Date 2021/3/17 9:32
 */
public class RoomEvent extends ApplicationEvent {

    public static final String PLAYER_JOIN = "playerJoin";

    public static final String PLAYER_LEVEL = "playerLevel";

    public static final String PUBLIC_MESSAGE = "publicMessage";

    private String eventType;

    private String roomId;

    private Integer player;

    private String message;

    public RoomEvent(String eventType, String roomId, Integer player) {
        super(roomId);
        this.eventType = eventType;
        this.roomId = roomId;
        this.player = player;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public Integer getPlayer() {
        return player;
    }

    public void setPlayer(Integer player) {
        this.player = player;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "RoomEvent{" +
            "eventType='" + eventType + '\'' +
            ", roomId='" + roomId + '\'' +
            ", player=" + player +
            '}';
    }
}
