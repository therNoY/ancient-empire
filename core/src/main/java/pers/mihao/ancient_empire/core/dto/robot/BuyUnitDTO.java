package pers.mihao.ancient_empire.core.dto.robot;

import pers.mihao.ancient_empire.base.bo.UnitInfo;

/**
 * @version 1.0
 * @author mihao
 * @date 2020\11\8 0008 13:20
 */
public class BuyUnitDTO {
    /**
     *
     * 要买的单位
     */
    private UnitInfo unitInfo;

    /**
     * 召唤的城堡
     */
    private CastleRegion castleRegion;

    public UnitInfo getUnitInfo() {
        return unitInfo;
    }

    public void setUnitInfo(UnitInfo unitInfo) {
        this.unitInfo = unitInfo;
    }

    public CastleRegion getCastleRegion() {
        return castleRegion;
    }

    public void setCastleRegion(CastleRegion castleRegion) {
        this.castleRegion = castleRegion;
    }
}
