package pers.mihao.ancient_empire.base.bo;

import pers.mihao.ancient_empire.base.util.AppUtil;

import java.io.Serializable;
import java.util.Arrays;

/**
 * 单位类 用来渲染地图 和 返回前端的 不包含 单位的基本信息
 */
public class Unit extends Site implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    /**
     * 类型
     */
    private String type;
    /**
     * UnitMes ID
     */
    private Integer typeId;

    /**
     * 单位血量
     */
    private Integer life;

    /**
     * 血量方便 [1, 0, 0] 前端展示
     */
    private Integer[] lifeNum;

    /**
     * 是否死亡
     */
    private Boolean dead;
    /**
     * 等级
     */
    private Integer level;
    /**
     * 是否死亡
     */
    private Boolean done;
    /**
     * 状态 buff 中毒
     */
    private String status;
    /**
     * 状态 存在的回合 超过两个回合 就会消失
     */
    private Integer statusPresenceNum;
    /**
     * 当前经验值
     */
    private Integer experience;

    /**
     * 是否是晋升单位
     */
    private Boolean promotion;

    public Unit() {
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
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

    public Integer[] getLifeNum() {
        return AppUtil.getArrayByInt(life);
    }

    public void setLifeNum(Integer[] lifeNum) {
        this.lifeNum = lifeNum;
    }

    public Boolean isDead() {
        return getDead();
    }

    public Boolean getDead() {
        return dead;
    }

    public void setDead(Boolean dead) {
        this.dead = dead;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Boolean isDone() {
        return done;
    }

    public Boolean getDone() {
        return done;
    }

    public void setDone(Boolean done) {
        this.done = done;
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

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getStatusPresenceNum() {
        return statusPresenceNum;
    }

    public void setStatusPresenceNum(Integer statusPresenceNum) {
        this.statusPresenceNum = statusPresenceNum;
    }

    public Boolean getPromotion() {
        return promotion;
    }

    public void setPromotion(Boolean promotion) {
        this.promotion = promotion;
    }

    public Integer getLife() {
        return life;
    }

    public void setLife(Integer life) {
        this.life = life;
    }

    @Override
    public String toString() {
        return "Unit{" +
                "typeId=" + typeId +
                ", lifeNum=" + Arrays.toString(lifeNum) +
                ", dead=" + dead +
                ", level=" + level +
                ", done=" + done +
                ", status='" + status + '\'' +
                ", statusPresenceNum=" + statusPresenceNum +
                ", experience=" + experience +
                ", promotion=" + promotion +
                ", row=" + row +
                ", column=" + column +
                '}';
    }
}
