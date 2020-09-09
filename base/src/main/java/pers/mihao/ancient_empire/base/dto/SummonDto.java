package pers.mihao.ancient_empire.base.dto;

import java.io.Serializable;
import pers.mihao.ancient_empire.base.bo.Position;
import pers.mihao.ancient_empire.base.bo.Unit;

public class SummonDto implements Serializable {

    public SummonDto() {
    }

    public SummonDto(String uuid, Integer index, LevelDto levelDto, Position tomb, Unit newUnit) {
        this.uuid = uuid;
        this.index = index;
        this.levelDto = levelDto;
        this.tomb = tomb;
        this.newUnit = newUnit;
    }

    private String uuid;
    private Integer index;
    private LevelDto levelDto;
    private Position tomb;
    private Unit newUnit;

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public LevelDto getLevelDto() {
        return levelDto;
    }

    public void setLevelDto(LevelDto levelDto) {
        this.levelDto = levelDto;
    }

    public Position getTomb() {
        return tomb;
    }

    public void setTomb(Position tomb) {
        this.tomb = tomb;
    }

    public Unit getNewUnit() {
        return newUnit;
    }

    public void setNewUnit(Unit newUnit) {
        this.newUnit = newUnit;
    }
}
