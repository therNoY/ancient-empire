package pers.mihao.ancient_empire.base.dto;

import java.io.Serializable;

public class LevelDto implements Serializable {
    protected Integer endExperience;
    protected Boolean leaveUp; // 攻击者是否升级

    public Integer getEndExperience() {
        return endExperience;
    }

    public void setEndExperience(Integer endExperience) {
        this.endExperience = endExperience;
    }

    public Boolean getLeaveUp() {
        return leaveUp;
    }

    public void setLeaveUp(Boolean leaveUp) {
        this.leaveUp = leaveUp;
    }
}
