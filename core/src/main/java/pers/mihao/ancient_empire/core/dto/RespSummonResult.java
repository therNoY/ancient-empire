package pers.mihao.ancient_empire.core.dto;

import pers.mihao.ancient_empire.common.bo.Unit;

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
