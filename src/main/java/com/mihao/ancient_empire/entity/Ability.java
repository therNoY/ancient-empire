package com.mihao.ancient_empire.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.mihao.ancient_empire.constant.AbilityEnum;

import java.io.Serializable;
import java.util.Objects;

/**
 * <p>
 * 单位信息表
 * </p>
 *
 * @author mihao
 * @since 2019-08-31
 */
public class Ability implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 能力类型
     */
    private String type;

    /**
     * 能力名称
     */
    private String name;

    /**
     * 能力说明
     */
    private String description;

    /**
     * 引用buff
     */
    private Integer buffId;

    public Ability() {
    }

    public Ability(String type) {
        this.type = type;
    }
    public Ability(AbilityEnum abilityEnum) {
        this.type = abilityEnum.type();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getBuffId() {
        return buffId;
    }

    public void setBuffId(Integer buffId) {
        this.buffId = buffId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Ability) {
            Ability a2 = (Ability) obj;
            if (a2.getType().equals(type)) {
                return true;
            }
        } else if (obj instanceof String) {
            String s = (String) obj;
            return this.getType().equals(s);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.type.hashCode();
    }

    @Override
    public String toString() {
        return "Ability{" +
                "id=" + id +
                ", type=" + type +
                ", name=" + name +
                ", description=" + description +
                ", buffId=" + buffId +
                "}";
    }
}
