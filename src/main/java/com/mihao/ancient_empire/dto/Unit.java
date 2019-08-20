package com.mihao.ancient_empire.dto;

import com.mihao.ancient_empire.common.util.StringUtil;

/**
 * 单位类 用来渲染地图 和 返回前端的 不包含 单位的基本信息
 */
public class Unit{

    private String id;
    // 类型
    private String type;
    // 血量 [1, 0, 0]
    private Integer[] life;
    // 当前行
    private Integer row;
    // 当前列
    private Integer column;
    // 是否已死
    private Boolean isDead;
    // 等级
    private Integer level;
    // 是否死亡
    private Boolean isDone;
    // 状态 buff 中毒
    private String status;
    // 经验值
    private Integer experience;

    public Unit(String type, Integer row, Integer column) {
        this.type = type;
        this.row = row;
        this.column = column;
        this.id = StringUtil.getUUID();
        this.life = new Integer[]{1, 0, 0};
        this.isDead = false;
        this.level = 0;
        this.isDone = false;
        this.experience = 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer[] getLife() {
        return life;
    }

    public void setLife(Integer[] life) {
        this.life = life;
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

    public Boolean getDead() {
        return isDead;
    }

    public void setDead(Boolean dead) {
        isDead = dead;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Boolean getDone() {
        return isDone;
    }

    public void setDone(Boolean done) {
        isDone = done;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getExperience() {
        return experience;
    }

    public void setExperience(Integer experience) {
        this.experience = experience;
    }
}
