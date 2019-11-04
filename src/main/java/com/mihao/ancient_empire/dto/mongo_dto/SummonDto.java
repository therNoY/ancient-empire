package com.mihao.ancient_empire.dto.mongo_dto;

import com.mihao.ancient_empire.dto.Position;
import com.mihao.ancient_empire.dto.Site;
import com.mihao.ancient_empire.dto.Unit;
import com.mihao.ancient_empire.dto.ws_dto.LevelDto;

import java.io.Serializable;

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
