package com.mihao.ancient_empire.ai.dto;

import com.mihao.ancient_empire.ai.constant.AiActiveEnum;
import com.mihao.ancient_empire.dto.Site;
import com.mihao.ancient_empire.dto.ws_dto.LifeChange;

import java.util.List;
import java.util.Map;

public class EndUnitResult extends ActiveResult{

    Map<Integer, List<LifeChange>> lifeChanges; // 改变 key armyIndex value

    public EndUnitResult(String id, Site site, Map<Integer, List<LifeChange>> lifeChanges) {
        super.setRecordId(id);
        super.setSite(site);
        super.setResultEnum(AiActiveEnum.END);
        this.lifeChanges = lifeChanges;
    }

    public Map<Integer, List<LifeChange>> getLifeChanges() {
        return lifeChanges;
    }

    public void setLifeChanges(Map<Integer, List<LifeChange>> lifeChanges) {
        this.lifeChanges = lifeChanges;
    }
}
