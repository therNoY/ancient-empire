package pers.mihao.ancient_empire.base.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 单位信息表 Id 属于物理主键 type和version属于逻辑主键
 * </p>
 *
 * @author mihao
 * @since 2020-09-23
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
     * 版本
     */
    private Integer version;

    /**
     * 是否发布 0为草稿 1正式
     */
    private Integer status;

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
    private Integer minAttachRange;

    /**
     * 最远的攻击距离
     */
    private Integer maxAttachRange;

    /**
     * 所占人口
     */
    private Integer population;

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
    private Integer tradable;

    /**
     * 是否是晋升单位，晋升单位无法购买
     */
    private Integer promotion;

    /**
     * 图片的索引 如果是新加的单位索引就是 新的的index
     * 更新版本如果不更换图片 保持原来的index 更换图片使用新版本ID的index
     */
    private String imgIndex;

    /**
     * 是否启用 0 不启用 1 启用
     */
    private Integer enable;


    private LocalDateTime createTime;

    private LocalDateTime updateTime;


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

    public Integer getMinAttachRange() {
        return minAttachRange;
    }

    public void setMinAttachRange(Integer minAttachRange) {
        this.minAttachRange = minAttachRange;
    }

    public Integer getMaxAttachRange() {
        return maxAttachRange;
    }

    public void setMaxAttachRange(Integer maxAttachRange) {
        this.maxAttachRange = maxAttachRange;
    }

    public Integer getPopulation() {
        return population;
    }

    public void setPopulation(Integer population) {
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

    public Integer getTradable() {
        return tradable;
    }

    public void setTradable(Integer tradable) {
        this.tradable = tradable;
    }

    public Integer getPromotion() {
        return promotion;
    }

    public void setPromotion(Integer promotion) {
        this.promotion = promotion;
    }

    public Integer getEnable() {
        return enable;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getImgIndex() {
        return imgIndex;
    }

    public void setImgIndex(String imgIndex) {
        this.imgIndex = imgIndex;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
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
            ", tradable=" + tradable +
            ", promotion=" + promotion +
            ", enable=" + enable +
            "}";
    }
}
