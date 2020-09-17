package pers.mihao.ancient_empire.core.manger.event;

import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.core.eums.GameEventEnum;

/**
 * 游戏事件
 *
 * @Author mh32736
 * @Date 2020/9/10 16:22
 */
public class GameEvent extends AbstractEvent {

    /*发起行动的位置*/
    private Site initiateSite;

    /* 被动接受的位置 */
    private Site passiveSite;

    /*游戏的事件*/
    private GameEventEnum eventEnum;

    public GameEventEnum getEventEnum() {
        return eventEnum;
    }

    public void setEventEnum(GameEventEnum eventEnum) {
        this.eventEnum = eventEnum;
    }

    public Site getInitiateSite() {
        return initiateSite;
    }

    public void setInitiateSite(Site initiateSite) {
        this.initiateSite = initiateSite;
    }

    public Site getPassiveSite() {
        return passiveSite;
    }

    public void setPassiveSite(Site passiveSite) {
        this.passiveSite = passiveSite;
    }
}
