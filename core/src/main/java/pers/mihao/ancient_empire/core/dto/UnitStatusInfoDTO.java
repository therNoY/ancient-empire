package pers.mihao.ancient_empire.core.dto;

/**
 * @version 1.0
 * @auther mihao
 * @date 2020\10\11 0011 20:11
 */
public class UnitStatusInfoDTO extends ArmyUnitIndexDTO{

    // 血量 [1, 0, 0]
    private Integer[] life;

    // 等级
    private Integer level;

    // 状态 buff 中毒
    private String status;

    // 经验值
    private Integer experience;

    private Boolean done;

    private Boolean updateCurr;

    public UnitStatusInfoDTO() {
    }

    public UnitStatusInfoDTO(ArmyUnitIndexDTO armyUnitIndexDTO) {
        super(armyUnitIndexDTO.getArmyIndex(), armyUnitIndexDTO.getUnitIndex());
    }

    public Boolean getUpdateCurr() {
        return updateCurr;
    }

    public void setUpdateCurr(Boolean updateCurr) {
        this.updateCurr = updateCurr;
    }

    public Integer[] getLife() {
        return life;
    }

    public void setLife(Integer[] life) {
        this.life = life;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
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

    public Boolean getDone() {
        return done;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }
}
