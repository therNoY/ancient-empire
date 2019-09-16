package com.mihao.ancient_empire.dto.ws_dto;

import com.mihao.ancient_empire.dto.Unit;

public class RespSummonResult extends LevelDto{

    private Integer armyIndex;
    private Unit bone;
    private SecondMoveDto secondMove;

    public Integer getArmyIndex() {
        return armyIndex;
    }

    public void setArmyIndex(Integer armyIndex) {
        this.armyIndex = armyIndex;
    }

    public Unit getBone() {
        return bone;
    }

    public void setBone(Unit bone) {
        this.bone = bone;
    }

    public SecondMoveDto getSecondMove() {
        return secondMove;
    }

    public void setSecondMove(SecondMoveDto secondMove) {
        this.secondMove = secondMove;
    }
}
