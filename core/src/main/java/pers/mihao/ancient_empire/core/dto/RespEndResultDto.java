package pers.mihao.ancient_empire.core.dto;


import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 行动结束对地图的影响
 */
public class RespEndResultDto implements Serializable {

    private String uuid; // 地图Id

    Map<Integer, List<LifeChange>> lifeChanges; // 改变 key armyIndex value

    private String unitId; // 单位移动的id

    private Integer row; // 最终的row

    private Integer column; // 最终的column

    TombChange tombChange; // 备用

    public Map<Integer, List<LifeChange>> getLifeChanges() {
        return lifeChanges;
    }

    public void setLifeChanges(Map<Integer, List<LifeChange>> lifeChanges) {
        this.lifeChanges = lifeChanges;
    }

    public TombChange getTombChange() {
        return tombChange;
    }

    public void setTombChange(TombChange tombChange) {
        this.tombChange = tombChange;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getRow() {
        return row;
    }

    public void setRow(Integer row) {
        this.row = row;
    }

    public Integer getColumn() {
        return column;
    }

    public void setColumn(Integer column) {
        this.column = column;
    }

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }
}
