package pers.mihao.ancient_empire.core.manger.event;

import pers.mihao.ancient_empire.auth.entity.User;
import pers.mihao.ancient_empire.core.eums.RoomEventEnum;

/**
 * @version 1.0
 * @author mihao
 * @date 2021\3\27 0027 22:42
 */
public class RoomEvent extends AbstractEvent{

    private RoomEventEnum eventType;

    /**
     * 军队颜色
     */
    private String armyColor;

    /**
     * 消息
     */
    private String message;

    public RoomEvent() {
    }

    public RoomEvent(String id, RoomEventEnum eventType, User user) {
        setId(id);
        this.eventType = eventType;
        setUser(user);
    }



    public RoomEventEnum getEventType() {
        return eventType;
    }

    public void setEventType(RoomEventEnum eventType) {
        this.eventType = eventType;
    }

    public String getArmyColor() {
        return armyColor;
    }

    public void setArmyColor(String armyColor) {
        this.armyColor = armyColor;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
