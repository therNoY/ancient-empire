package pers.mihao.ancient_empire.base.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

/**
 * <p>
 * 单位信息表
 * </p>
 *
 * @author mihao
 * @since 2020-09-22
 */
public class UnitMes implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 单位类型
     */
    private String type;

    /**
     * 单位名称
     */
    private String name;

    /**
     * 攻击类型 物理 魔法
     */
    private String attackType;

    /**
     * 购买价格
     */
    private Integer price;

    /**
     * 最近的攻击距离
     */
    private Boolean minAttachRange;

    /**
     * 最远的攻击距离
     */
    private Boolean maxAttachRange;

    /**
     * 所占人口
     */
    private Boolean population;

    /**
     * 单位描述
     */
    private String description;

    /**
     * 创建者Id
     */
    private Integer createUserId;

    /**
     * 是否是可以购买的0 不可 1可以
     */
    private Integer tradeable;

    /**
     * 是否是晋升单位，晋升单位无法购买
     */
    private Integer promotion;

    /**
     * 是否启用 0 不启用 1 启用
     */
    private Boolean enable;

    /**
     * 模板id
     */
    private Integer tempId;

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
    public String getAttackType() {
        return attackType;
    }

    public void setAttackType(String attackType) {
        this.attackType = attackType;
    }
    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }
    public Boolean getMinAttachRange() {
        return minAttachRange;
    }

    public void setMinAttachRange(Boolean minAttachRange) {
        this.minAttachRange = minAttachRange;
    }
    public Boolean getMaxAttachRange() {
        return maxAttachRange;
    }

    public void setMaxAttachRange(Boolean maxAttachRange) {
        this.maxAttachRange = maxAttachRange;
    }
    public Boolean getPopulation() {
        return population;
    }

    public void setPopulation(Boolean population) {
        this.population = population;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public Integer getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Integer createUserId) {
        this.createUserId = createUserId;
    }
    public Integer getTradeable() {
        return tradeable;
    }

    public void setTradeable(Integer tradeable) {
        this.tradeable = tradeable;
    }
    public Integer getPromotion() {
        return promotion;
    }

    public void setPromotion(Integer promotion) {
        this.promotion = promotion;
    }
    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }
    public Integer getTempId() {
        return tempId;
    }

    public void setTempId(Integer tempId) {
        this.tempId = tempId;
    }

    @Override
    public String toString() {
        return "UnitMes{" +
        "id=" + id +
        ", type=" + type +
        ", name=" + name +
        ", attackType=" + attackType +
        ", price=" + price +
        ", minAttachRange=" + minAttachRange +
        ", maxAttachRange=" + maxAttachRange +
        ", population=" + population +
        ", description=" + description +
        ", createUserId=" + createUserId +
        ", tradeable=" + tradeable +
        ", promotion=" + promotion +
        ", enable=" + enable +
        ", tempId=" + tempId +
        "}";
    }
}
