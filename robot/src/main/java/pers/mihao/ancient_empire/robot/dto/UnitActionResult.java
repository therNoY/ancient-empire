package pers.mihao.ancient_empire.robot.dto;

import pers.mihao.ancient_empire.robot.constant.AiActiveEnum;
import pers.mihao.ancient_empire.common.bo.Position;
import pers.mihao.ancient_empire.common.bo.Site;
import pers.mihao.ancient_empire.common.bo.Unit;
import pers.mihao.ancient_empire.common.bo.ws_dto.PathPosition;
import com.mihao.ancient_empire.util.AppUtil;

import java.util.List;

/**
 * 有行动的结果
 */
public class UnitActionResult extends ActiveResult {

    private Unit selectUnit; /* 设置当前单位 */

    private Unit unit; /* 目标单位 */

    private List<Position> moveArea;

    private List<PathPosition> pathPositions;


    public UnitActionResult(Unit selectUnit, String recordId, AiActiveEnum actionEnum, Site site) {
        this.setSelectUnit(selectUnit);
        super.setRecordId(recordId);
        super.setSite(site);
        super.setResultEnum(actionEnum);

    }

    public UnitActionResult(String recordId, AiActiveEnum actionEnum, Site site) {
        this(recordId, actionEnum, site, null);
    }

    public UnitActionResult(String recordId, AiActiveEnum actionEnum, Site site, Unit unit) {
        super.setRecordId(recordId);
        super.setSite(site);
        super.setResultEnum(actionEnum);
        this.unit = unit;
    }

    public UnitActionResult(String recordId, AiActiveEnum actionEnum, Site site, Unit unit, List<Position> moveArea) {
        super.setRecordId(recordId);
        super.setSite(site);
        super.setResultEnum(actionEnum);
        this.unit = unit;
        this.moveArea = moveArea;
    }


    public Unit getSelectUnit() {
        return selectUnit;
    }

    public void setSelectUnit(Unit selectUnit) {
        this.selectUnit = selectUnit;
    }

    public List<Position> getMoveArea() {
        return moveArea;
    }

    public void setMoveArea(List<Position> moveArea) {
        this.moveArea = moveArea;
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

    public int getLength() {
        return AppUtil.getLength(AppUtil.getPosition(unit), getSite());
    }
}
