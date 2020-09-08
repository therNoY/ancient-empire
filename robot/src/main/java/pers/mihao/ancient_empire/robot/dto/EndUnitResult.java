package pers.mihao.ancient_empire.robot.dto;

import pers.mihao.ancient_empire.robot.constant.AiActiveEnum;
import pers.mihao.ancient_empire.common.bo.Site;
import pers.mihao.ancient_empire.common.bo.ws_dto.LifeChange;

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
