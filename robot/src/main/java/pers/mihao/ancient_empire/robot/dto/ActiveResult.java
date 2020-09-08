package pers.mihao.ancient_empire.robot.dto;

import pers.mihao.ancient_empire.robot.constant.AiActiveEnum;
import pers.mihao.ancient_empire.common.bo.Site;

public class ActiveResult {

    private String recordId;

    private AiActiveEnum resultEnum;

    private Site site;


    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public AiActiveEnum getResultEnum() {
        return resultEnum;
    }

    public void setResultEnum(AiActiveEnum resultEnum) {
        this.resultEnum = resultEnum;
    }
}
