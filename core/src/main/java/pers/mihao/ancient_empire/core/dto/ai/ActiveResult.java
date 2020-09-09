package pers.mihao.ancient_empire.core.dto.ai;

import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.core.eums.ai.AiActiveEnum;

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
