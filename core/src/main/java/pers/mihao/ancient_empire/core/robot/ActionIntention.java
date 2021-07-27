package pers.mihao.ancient_empire.core.robot;

import pers.mihao.ancient_empire.base.bo.RegionInfo;
import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.base.bo.UnitInfo;

/**
 * robot的行动意向
 *
 * @Author mihao
 * @Date 2020/11/7 14:10
 */
public class ActionIntention {

    /**
     * 结果枚举类
     */
    private RobotActiveEnum active;

    /**
     * 目标地点
     */
    private Site site;

    /**
     * 目标单位
     */
    private UnitInfo aimUnit;

    /**
     * 目标地形
     */
    private RegionInfo aimRegion;

    public ActionIntention(RobotActiveEnum active, Site site) {
        this.active = active;
        this.site = site;
    }

    public ActionIntention(RobotActiveEnum active, Site site, UnitInfo aimUnit) {
        this.active = active;
        this.site = site;
        this.aimUnit = aimUnit;
    }

    public ActionIntention(RobotActiveEnum active, RegionInfo aimRegion) {
        this.active = active;
        this.aimRegion = aimRegion;
        this.site = new Site(aimRegion.getRow(), aimRegion.getColumn());
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public RobotActiveEnum getActive() {
        return active;
    }

    public void setActive(RobotActiveEnum active) {
        this.active = active;
    }

    public UnitInfo getAimUnit() {
        return aimUnit;
    }

    public void setAimUnit(UnitInfo aimUnit) {
        this.aimUnit = aimUnit;
    }

    public RegionInfo getAimRegion() {
        return aimRegion;
    }

    public void setAimRegion(RegionInfo aimRegion) {
        this.aimRegion = aimRegion;
    }

    @Override
    public String toString() {
        return "ActionIntention{" +
            "resultEnum=" + active +
            ", site=" + site +
            ", aimUnit=" + aimUnit +
            ", aimRegion=" + aimRegion +
            '}';
    }
}
