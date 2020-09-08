package pers.mihao.ancient_empire.core.dto;

import java.io.Serializable;

/**
 * 攻击结果
 */
public class AttachResult  implements Serializable {

    private Integer[] attach; // 被攻击者受到伤害
    private Boolean dead; // 被攻击者是否死
    private Boolean haveTomb; // 被攻击者死了有坟墓

    private Integer[] lastLife; // 被攻击者剩余血量
    private String endStatus; // 被攻击者的状态 可能会中毒

    private Integer endExperience; // 结束时的经验
    private Boolean leaveUp; // 攻击者是否升级

    public Integer[] getAttach() {
        return attach;
    }

    public void setAttach(Integer[] attach) {
        this.attach = attach;
    }

    public Boolean getDead() {
        return dead;
    }

    public void setDead(Boolean dead) {
        this.dead = dead;
    }

    public Integer[] getLastLife() {
        return lastLife;
    }

    public void setLastLife(Integer[] lastLife) {
        this.lastLife = lastLife;
    }

    public Boolean getHaveTomb() {
        return haveTomb;
    }

    public void setHaveTomb(Boolean haveTomb) {
        this.haveTomb = haveTomb;
    }

    public Boolean getLeaveUp() {
        return leaveUp;
    }

    public void setLeaveUp(Boolean leaveUp) {
        this.leaveUp = leaveUp;
    }

    public String getEndStatus() {
        return endStatus;
    }

    public void setEndStatus(String endStatus) {
        this.endStatus = endStatus;
    }

    public Integer getEndExperience() {
        return endExperience;
    }

    public void setEndExperience(Integer endExperience) {
        this.endExperience = endExperience;
    }
}
