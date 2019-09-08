package com.mihao.ancient_empire.dto.ws_dto;

/**
 * 记录攻击情况 1 有增加 0 无增加 -1 有减少
 */
public class AttachSituation {
    private int attachUp;
    private int defenseUp;

    private int beAttackUp;
    private int beDefenseUp;

    public int getAttachUp() {
        return attachUp;
    }

    public void setAttachUp(int attachUp) {
        this.attachUp = attachUp;
    }

    public int getDefenseUp() {
        return defenseUp;
    }

    public void setDefenseUp(int defenseUp) {
        this.defenseUp = defenseUp;
    }

    public int getBeAttackUp() {
        return beAttackUp;
    }

    public void setBeAttackUp(int beAttackUp) {
        this.beAttackUp = beAttackUp;
    }

    public int getBeDefenseUp() {
        return beDefenseUp;
    }

    public void setBeDefenseUp(int beDefenseUp) {
        this.beDefenseUp = beDefenseUp;
    }
}
