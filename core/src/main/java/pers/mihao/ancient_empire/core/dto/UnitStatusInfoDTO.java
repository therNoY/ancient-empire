package pers.mihao.ancient_empire.core.dto;

import pers.mihao.ancient_empire.base.util.AppUtil;

import java.util.Arrays;

/**
 * @author mihao
 * @version 1.0
 * @date 2020\10\11 0011 20:11
 */
public class UnitStatusInfoDTO extends ArmyUnitIndexDTO{

    /**
     * 血量
     */
    private Integer life;

    /**
     * 前端展示
     */
    private Integer[] lifeNum;

    /**
     * 等级
     */
    private Integer level;

    /**
     * 状态 buff 中毒
     */
    private String status;

    /**
     * 经验值
     */
    private Integer experience;

    /**
     * 是否结束
     */
    private Boolean done;

    /**
     * 是否更新当前位置
     */
    private Boolean updateCurr;

    public UnitStatusInfoDTO() {
    }

    public UnitStatusInfoDTO(Integer armyIndex, Integer unitIndex) {
        super(armyIndex, unitIndex);
    }

    public UnitStatusInfoDTO(ArmyUnitIndexDTO armyUnitIndexDTO) {
        super(armyUnitIndexDTO.getArmyIndex(), armyUnitIndexDTO.getUnitIndex());
    }

    public Boolean getUpdateCurr() {
        return updateCurr;
    }

    public UnitStatusInfoDTO setUpdateCurr(Boolean updateCurr) {
        this.updateCurr = updateCurr;
        return this;
    }

    public Integer getLife() {
        return life;
    }

    public UnitStatusInfoDTO setLife(Integer life) {
        this.life = life;
        setLifeNum(AppUtil.getArrayByInt(life));
        return this;

    }

    public Integer getLevel() {
        return level;
    }

    public UnitStatusInfoDTO setLevel(Integer level) {
        this.level = level;
        return this;

    }

    public String getStatus() {
        return status;
    }

    public UnitStatusInfoDTO setStatus(String status) {
        this.status = status;
        return this;

    }

    public Integer getExperience() {
        return experience;
    }

    public UnitStatusInfoDTO setExperience(Integer experience) {
        this.experience = experience;
        return this;
    }

    public Boolean getDone() {
        return done;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }

    public Integer[] getLifeNum() {
        return lifeNum;
    }

    public void setLifeNum(Integer[] lifeNum) {
        this.lifeNum = lifeNum;
    }

    @Override
    public String toString() {

        return "UnitStatusInfoDTO{" +
            "life=" + life +
            ", level=" + level +
            ", status='" + status + '\'' +
            ", experience=" + experience +
            ", done=" + done +
            ", updateCurr=" + updateCurr +
            '}';
    }
}
