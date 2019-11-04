package com.mihao.ancient_empire.ai.dto;

import com.mihao.ancient_empire.ai.constant.AiActiveEnum;
import com.mihao.ancient_empire.dto.Site;

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
