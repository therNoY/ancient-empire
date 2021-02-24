package pers.mihao.ancient_empire.base.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

/**
 * <p>
 * 地形信息表
 * </p>
 *
 * @author mihao
 * @since 2020-09-23
 */
public class RegionMes implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 地图类型
     */
    private String type;

    /**
     * 地形的名称
     */
    private String name;

    /**
     * 增加的防御力
     */
    private Integer buff;

    /**
     * 恢复
     */
    private Integer restore;

    /**
     * 每回合收的金币
     */
    private Integer tax;

    /**
     * 消耗的移动力
     */
    private Integer deplete;

    /**
     * 描述
     */
    private String description;

    /**
     * 创建者Id
     */
    private Integer createUserId;


    /**
     * 是否启用 0 不启用 1 启用
     */
    private Integer enable;

    /**
     * 是否净化
     */
    private Integer purify;

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
    public Integer getBuff() {
        return buff;
    }

    public void setBuff(Integer buff) {
        this.buff = buff;
    }
    public Integer getRestore() {
        return restore;
    }

    public void setRestore(Integer restore) {
        this.restore = restore;
    }
    public Integer getTax() {
        return tax;
    }

    public void setTax(Integer tax) {
        this.tax = tax;
    }
    public Integer getDeplete() {
        return deplete;
    }

    public void setDeplete(Integer deplete) {
        this.deplete = deplete;
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
    public Integer getEnable() {
        return enable;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
    }

    public Integer getPurify() {
        return purify;
    }

    public void setPurify(Integer purify) {
        this.purify = purify;
    }

    @Override
    public String toString() {
        return "RegionMes{" +
        "id=" + id +
        ", type=" + type +
        ", name=" + name +
        ", buff=" + buff +
        ", restore=" + restore +
        ", tax=" + tax +
        ", deplete=" + deplete +
        ", description=" + description +
        ", createUserId=" + createUserId +
        ", enable=" + enable +
        "}";
    }
}
