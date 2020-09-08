package pers.mihao.ancient_empire.core.dto;

import pers.mihao.ancient_empire.common.bo.Position;

import java.io.Serializable;
import java.util.List;

/**
 * 返回的攻击结果的dto
 */
public class RespAttachResultDto implements Serializable {

    private AttachResult attachResult; // 攻击结果
    private Boolean counterattack; //是否被反击
    private AttachResult counterattackResult; // 反击结果
    private AttachSituation attachSituation; // 一次攻击加成结果
    private Boolean secondMove; // 单位是否有二次移动
    private List<Position> moveArea; // 单位二次移动的移动范围

    public AttachResult getAttachResult() {
        return attachResult;
    }

    public void setAttachResult(AttachResult attachResult) {
        this.attachResult = attachResult;
    }

    public Boolean getCounterattack() {
        return counterattack;
    }

    public void setCounterattack(Boolean counterattack) {
        this.counterattack = counterattack;
    }

    public AttachResult getCounterattackResult() {
        return counterattackResult;
    }

    public void setCounterattackResult(AttachResult counterattackResult) {
        this.counterattackResult = counterattackResult;
    }

    public AttachSituation getAttachSituation() {
        return attachSituation;
    }

    public void setAttachSituation(AttachSituation attachSituation) {
        this.attachSituation = attachSituation;
    }

    public Boolean getSecondMove() {
        return secondMove;
    }

    public void setSecondMove(Boolean secondMove) {
        this.secondMove = secondMove;
    }

    public List<Position> getMoveArea() {
        return moveArea;
    }

    public void setMoveArea(List<Position> moveArea) {
        this.moveArea = moveArea;
    }
}
