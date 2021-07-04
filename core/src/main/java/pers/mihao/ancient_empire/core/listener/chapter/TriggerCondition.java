package pers.mihao.ancient_empire.core.listener.chapter;

import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.core.listener.chapter.enums.TriggerTypeEnum;

/**
 * @Author mihao
 * @Date 2021/4/20 13:10
 */
public class TriggerCondition {

    /**
     * 触发类型
     */
    private TriggerTypeEnum triggerType;

    /**
     * 在范围内触发的话 需要的起始点
     */
    private Site minSite;

    /**
     * 返回吃法结束点
     */
    private Site maxSite;

    private Integer round;

    public Integer getRound() {
        return round;
    }

    public void setRound(Integer round) {
        this.round = round;
    }

    public TriggerCondition(TriggerTypeEnum triggerType) {
        this.triggerType = triggerType;
    }

    public TriggerTypeEnum getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(TriggerTypeEnum triggerType) {
        this.triggerType = triggerType;
    }

    public Site getMinSite() {
        return minSite;
    }

    public void setMinSite(Site minSite) {
        this.minSite = minSite;
    }

    public Site getMaxSite() {
        return maxSite;
    }

    public void setMaxSite(Site maxSite) {
        this.maxSite = maxSite;
    }
}
