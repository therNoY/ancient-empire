package pers.mihao.ancient_empire.core.dto;

import java.io.Serializable;

/**
 * 攻击结果
 * @version 1.0
 * @author mihao
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

}
