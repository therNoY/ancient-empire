package pers.mihao.ancient_empire.base.bo;

/**
 * 单位类 用来渲染地图 和 返回前端的 不包含 单位的基本信息
 */
public class Unit extends Site {

    private String id;
    // 类型
    private String type;
    // 类型ID 寻找图片
    private Integer typeId;
    // 血量 [1, 0, 0]
    private Integer[] life;
    // 是否已死
    private Boolean isDead;
    // 等级
    private Integer level;
    // 是否死亡
    private Boolean isDone;
    // 状态 buff 中毒
    private String status;
    // 状态 存在的回合 超过两个回合 就会消失
    private Integer statusPresenceNum;
    // 经验值
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

    public Integer[] getLife() {
        return life;
    }

    public void setLife(Integer[] life) {
        this.life = life;
    }

    public Boolean isDead() {
        return getDead();
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

    public Boolean isDone() {
        return isDone;
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

    @Override
    public String toString() {
        return "Unit{" +
                "type='" + type + '\'' +
                ", row='" + row + '\'' +
                ", column='" + column + '\'' +
                '}';
    }
}
