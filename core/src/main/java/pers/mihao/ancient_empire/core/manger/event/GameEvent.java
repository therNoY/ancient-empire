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
    /*游戏ID*/
    private String gameId;

    /*登录人ID*/
    private String userId;

    /*发起行动的位置*/
    private Site initiateSite;

    /* 被动接受的位置 */
    private Site passiveSite;

    /*游戏的事件*/
    private GameEventEnum event;

    public GameEventEnum getEvent() {
        return event;
    }

    public void setEvent(GameEventEnum event) {
        this.event = event;
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

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
