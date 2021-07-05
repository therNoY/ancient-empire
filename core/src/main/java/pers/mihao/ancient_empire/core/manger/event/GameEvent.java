package pers.mihao.ancient_empire.core.manger.event;

import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.core.eums.GameEventEnum;

/**
 * 游戏事件
 *
 * @author mihao
 * @date 2020/9/10 16:22
 */
public class GameEvent extends AbstractEvent {

    /**
     * 发起行动的位置
     */
    private Site initiateSite;

    /**
     * 被动接受的位置
     */
    private Site aimSite;

    /**
     * 单位ID
     */
    private Integer unitId;

    /**
     * 地形的index
     */
    private Integer regionIndex;

    /**
     * 游戏的事件
     */
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

    public Site getAimSite() {
        return aimSite;
    }

    public void setAimSite(Site aimSite) {
        this.aimSite = aimSite;
    }

    public Integer getRegionIndex() {
        return regionIndex;
    }

    public void setRegionIndex(Integer regionIndex) {
        this.regionIndex = regionIndex;
    }

    public Integer getUnitId() {
        return unitId;
    }

    public void setUnitId(Integer unitId) {
        this.unitId = unitId;
    }

    @Override
    public String toString() {
        return "GameEvent{" +
                ", initiateSite=" + initiateSite +
                ", aimSite=" + aimSite +
                ", regionIndex=" + regionIndex +
                ", event=" + event +
                '}';
    }
}
