package pers.mihao.ancient_empire.core.robot;

import pers.mihao.ancient_empire.base.bo.RegionInfo;
import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.base.bo.UnitInfo;

/**
 * robot的行动意向
 * @Author mh32736
 * @Date 2020/11/7 14:10
 */
public class ActionIntention {

    /**
     * 结果枚举类
     */
    private RobotActiveEnum resultEnum;

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

    public ActionIntention(RobotActiveEnum resultEnum, Site site) {
        this.resultEnum = resultEnum;
        this.site = site;
    }

    public ActionIntention(RobotActiveEnum resultEnum, Site site, UnitInfo aimUnit) {
        this.resultEnum = resultEnum;
        this.site = site;
        this.aimUnit = aimUnit;
    }

    public ActionIntention(RobotActiveEnum resultEnum, RegionInfo aimRegion) {
        this.resultEnum = resultEnum;
        this.aimRegion = aimRegion;
        this.site = new Site(aimRegion.getRow(), aimRegion.getColumn());
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public RobotActiveEnum getResultEnum() {
        return resultEnum;
    }

    public void setResultEnum(RobotActiveEnum resultEnum) {
        this.resultEnum = resultEnum;
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
            "resultEnum=" + resultEnum +
            ", site=" + site +
            ", aimUnit=" + aimUnit.simpleInfoShow() +
            ", aimRegion=" + aimRegion +
            '}';
    }
}
