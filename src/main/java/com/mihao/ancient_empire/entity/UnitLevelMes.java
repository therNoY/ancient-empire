package com.mihao.ancient_empire.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import javax.validation.constraints.*;
import java.io.Serializable;

/**
 * <p>
 * 单位等级信息表
 * </p>
 *
 * @author mihao
 * @since 2019-08-11
 */
public class UnitLevelMes implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    @NotNull(message = "Id不能为空")
    private Integer id;

    /**
     * 单位Id
     */
    @NotNull(message = "单位Id 不能为空")
    private Integer unitId;

    /**
     * 等级
     */
    @DecimalMax(value = "3", message = "单位等级必须在0和3之间")
    @DecimalMin(value = "0", message = "单位等级必须在0和3之间")
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getUnitId() {
        return unitId;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public void setUnitId(Integer unitId) {
        this.unitId = unitId;
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

    @Override
    public String toString() {
        return "UnitLevelMes{" +
        "id=" + id +
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
