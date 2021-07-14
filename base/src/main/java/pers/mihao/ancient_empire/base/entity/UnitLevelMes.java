package pers.mihao.ancient_empire.base.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

/**
 * <p>
 * 单位等级信息表
 * </p>
 *
 * @author mihao
 * @since 2020-09-23
 */
public class UnitLevelMes implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 单位Id
     */
    @TableId
    private Integer unitId;

    /**
     * 等级
     */
    @TableId
    private Integer level;

    /**
     * 最低攻击力
     */
    private Integer minAttack;

    /**
     * 最高攻击力
     */
    private Integer maxAttack;

    /**
     * 护甲
     */
    private Integer physicalDefense;

    /**
     * 魔法防御
     */
    private Integer magicDefense;

    /**
     * 移动力
     */
    private Integer speed;

    /**
     * 最大生命值
     */
    private Integer maxLife;

    public Integer getUnitId() {
        return unitId;
    }

    public void setUnitId(Integer unitId) {
        this.unitId = unitId;
    }
    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }
    public Integer getMinAttack() {
        return minAttack;
    }

    public void setMinAttack(Integer minAttack) {
        this.minAttack = minAttack;
    }
    public Integer getMaxAttack() {
        return maxAttack;
    }

    public void setMaxAttack(Integer maxAttack) {
        this.maxAttack = maxAttack;
    }
    public Integer getPhysicalDefense() {
        return physicalDefense;
    }

    public void setPhysicalDefense(Integer physicalDefense) {
        this.physicalDefense = physicalDefense;
    }
    public Integer getMagicDefense() {
        return magicDefense;
    }

    public void setMagicDefense(Integer magicDefense) {
        this.magicDefense = magicDefense;
    }
    public Integer getSpeed() {
        return speed;
    }

    public void setSpeed(Integer speed) {
        this.speed = speed;
    }

    public Integer getMaxLife() {
        return maxLife;
    }

    public void setMaxLife(Integer maxLife) {
        this.maxLife = maxLife;
    }

    @Override
    public String toString() {
        return "UnitLevelMes{" +
        ", unitId=" + unitId +
        ", level=" + level +
        ", minAttack=" + minAttack +
        ", maxAttack=" + maxAttack +
        ", physicalDefense=" + physicalDefense +
        ", magicDefense=" + magicDefense +
        ", speed=" + speed +
        "}";
    }
}
