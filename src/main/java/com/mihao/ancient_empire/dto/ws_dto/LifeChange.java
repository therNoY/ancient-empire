package com.mihao.ancient_empire.dto.ws_dto;

import java.io.Serializable;

public class LifeChange implements Serializable {

    private Integer index;
    private Integer[] change;
    private String state;
    private boolean dead;
    private boolean haveTomb;
    private Integer[] lastLife;

    public LifeChange() {
    }

    public LifeChange(Integer index) {
        this.index = index;
    }

    public LifeChange(Integer index, Integer[] change) {
        this.index = index;
        this.change = change;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Integer[] getChange() {
        return change;
    }

    public void setChange(Integer[] change) {
        this.change = change;
    }


    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public boolean isHaveTomb() {
        return haveTomb;
    }

    public void setHaveTomb(boolean haveTomb) {
        this.haveTomb = haveTomb;
    }

    public Integer[] getLastLife() {
        return lastLife;
    }

    public void setLastLife(Integer[] lastLife) {
        this.lastLife = lastLife;
    }
}
