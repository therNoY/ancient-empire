package pers.mihao.ancient_empire.core.dto;

import pers.mihao.ancient_empire.common.bo.Unit;

/**
 * 客户端 查询攻击结果需要的dto
 */
public class ReqAttachDto extends ReqSecondMoveDto{

    private Unit attachUnit;
    private Unit beAttachUnit;

    public Unit getAttachUnit() {
        return attachUnit;
    }

    public void setAttachUnit(Unit attachUnit) {
        this.attachUnit = attachUnit;
    }

    public Unit getBeAttachUnit() {
        return beAttachUnit;
    }

    public void setBeAttachUnit(Unit beAttachUnit) {
        this.beAttachUnit = beAttachUnit;
    }
}
