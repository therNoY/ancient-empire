package com.mihao.ancient_empire.ai.dto;

import com.mihao.ancient_empire.ai.constant.AiActiveEnum;
import com.mihao.ancient_empire.dto.Site;
import com.mihao.ancient_empire.dto.Unit;
import com.mihao.ancient_empire.dto.ws_dto.PathPosition;

import java.util.List;

/**
 * 有行动的结果
 */
public class UnitActionResult extends ActiveResult {


    private Unit unit;

    private List<PathPosition> pathPositions;

    public UnitActionResult(String recordId, AiActiveEnum actionEnum, Site site) {
        this(recordId, actionEnum, site, null);
    }

    public UnitActionResult(String recordId, AiActiveEnum actionEnum, Site site, Unit unit) {
        super.setRecordId(recordId);
        super.setSite(site);
        super.setResultEnum(actionEnum);
        this.unit = unit;
    }


    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public List<PathPosition> getPathPositions() {
        return pathPositions;
    }

    public void setPathPositions(List<PathPosition> pathPositions) {
        this.pathPositions = pathPositions;
    }
}
