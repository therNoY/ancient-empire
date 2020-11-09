package pers.mihao.ancient_empire.core.robot;

import pers.mihao.ancient_empire.base.bo.Region;
import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.base.bo.UnitInfo;
import pers.mihao.ancient_empire.core.eums.ai.AiActiveEnum;

/**
 * robot的行动意向
 * @Author mh32736
 * @Date 2020/11/7 14:10
 */
public class ActionIntention {

    private RobotActiveEnum resultEnum;

    private Site site;


    private UnitInfo aimUnit;

    private Region aimRegion;

    public ActionIntention(RobotActiveEnum resultEnum, Site site) {
        this.resultEnum = resultEnum;
        this.site = site;
    }

    public ActionIntention(RobotActiveEnum resultEnum, Site site, UnitInfo aimUnit) {
        this.resultEnum = resultEnum;
        this.site = site;
        this.aimUnit = aimUnit;
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

    public Region getAimRegion() {
        return aimRegion;
    }

    public void setAimRegion(Region aimRegion) {
        this.aimRegion = aimRegion;
    }
}
