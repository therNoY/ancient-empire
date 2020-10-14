package pers.mihao.ancient_empire.core.dto;

import java.io.Serializable;
import java.util.List;
import pers.mihao.ancient_empire.base.bo.Position;

/**
 * 返回的攻击结果的dto
 */
public class RespAttachResultDto implements Serializable {

    private AttachResult attachResult; // 攻击结果
    private Boolean antiAttack; //是否被反击
    private AttachResult antiAttackResult; // 反击结果
    private AttachSituation attachSituation; // 一次攻击加成结果
    private Boolean secondMove; // 单位是否有二次移动
    private List<Position> moveArea; // 单位二次移动的移动范围

    public AttachResult getAttachResult() {
        return attachResult;
    }

    public void setAttachResult(AttachResult attachResult) {
        this.attachResult = attachResult;
    }

    public Boolean getAntiAttack() {
        return antiAttack;
    }

    public void setAntiAttack(Boolean antiAttack) {
        this.antiAttack = antiAttack;
    }

    public AttachResult getAntiAttackResult() {
        return antiAttackResult;
    }

    public void setAntiAttackResult(AttachResult antiAttackResult) {
        this.antiAttackResult = antiAttackResult;
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
