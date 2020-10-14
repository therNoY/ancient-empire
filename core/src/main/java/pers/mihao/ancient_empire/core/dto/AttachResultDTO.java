package pers.mihao.ancient_empire.core.dto;

import pers.mihao.ancient_empire.base.bo.Site;

import java.io.Serializable;
import java.util.List;

/**
 * 攻击结果
 * @version 1.0
 * @auther mihao
 * @date 2020\10\7 0007 19:07
 */
public class AttachResultDTO implements Serializable {

    /**
     * 攻击结果
     */
    private AttachResult attachResult;
    /**
     * 是否被反击
     */
    private Boolean antiAttack;

    /**
     * 反击结果
     */
    private AttachResult antiAttackResult;

    /**
     * 单位二次移动的移动范围
     */
    private List<Site> moveArea;

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

    public List<Site> getMoveArea() {
        return moveArea;
    }

    public void setMoveArea(List<Site> moveArea) {
        this.moveArea = moveArea;
    }
}
