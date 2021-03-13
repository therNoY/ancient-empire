package pers.mihao.ancient_empire.base.event;

import org.springframework.context.ApplicationEvent;

/**
 * @Author mh32736
 * @Date 2021/3/11 10:21
 */
public class PlayerLevelRoomEvent extends ApplicationEvent {

    public PlayerLevelRoomEvent(Object source) {
        super(source);
    }
}
