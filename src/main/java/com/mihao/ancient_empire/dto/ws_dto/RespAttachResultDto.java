package com.mihao.ancient_empire.dto.ws_dto;

/**
 * 返回的攻击结果的dto
 */
public class RespAttachResultDto {

    private AttachResult attachResult; // 攻击结果
    private Boolean counterattack; //是否被反击
    private AttachResult counterattackResult; // 反击结果

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
}
