package pers.mihao.ancient_empire.base.event;

import org.springframework.context.ApplicationEvent;

/**
 * @Author mh32736
 * @Date 2021/3/11 10:13
 */
public class PlayerJoinRoomEvent extends ApplicationEvent {

    public PlayerJoinRoomEvent(Object source) {
        super(source);
    }


}
