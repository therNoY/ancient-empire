package pers.mihao.ancient_empire.base.event;

import org.springframework.context.ApplicationEvent;

/**
 * @Author mh32736
 * @Date 2021/3/17 9:32
 */
public class AppRoomEvent extends ApplicationEvent {

    public static final String PLAYER_JOIN = "playerJoin";

    public static final String PLAYER_LEVEL = "playerLevel";

    public static final String PUBLIC_MESSAGE = "publicMessage";

    public static final String CHANG_ARMY = "changeArmy";

    private String eventType;

    private String roomId;

    private Integer player;

    private String message;

    /**
     * 加入的军队
     */
    private String joinArmy;

    /**
     * 离开的军队
     */
    private String levelArmy;

    public AppRoomEvent(String eventType, String roomId, Integer player) {
        super(roomId);
        this.eventType = eventType;
        this.roomId = roomId;
        this.player = player;
    }

    public AppRoomEvent(String eventType, String roomId, String message) {
        super(roomId);
        this.eventType = eventType;
        this.roomId = roomId;
        this.message = message;
    }

    public AppRoomEvent(String eventType, String roomId) {
        super(roomId);
        this.eventType = eventType;
        this.roomId = roomId;
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

    public String getJoinArmy() {
        return joinArmy;
    }

    public void setJoinArmy(String joinArmy) {
        this.joinArmy = joinArmy;
    }

    public String getLevelArmy() {
        return levelArmy;
    }

    public void setLevelArmy(String levelArmy) {
        this.levelArmy = levelArmy;
    }

    @Override
    public String toString() {
        return "AppRoomEvent{" +
            "eventType='" + eventType + '\'' +
            ", roomId='" + roomId + '\'' +
            ", player=" + player +
            '}';
    }
}
