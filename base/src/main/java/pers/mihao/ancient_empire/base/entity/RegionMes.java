package pers.mihao.ancient_empire.base.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * <p>
 * 地形信息表
 * </p>
 *
 * @author mihao
 * @since 2019-08-11
 */
public class RegionMes implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    @DecimalMin(value = "0", message = "ID 必须是正数")
    private Integer id;

    /**
     * 类型
     */
    @NotBlank(message = "类型不能为空")
    private String type;

    /**
     * 地形的名称
     */
    @NotBlank(message = "名称不能为空")
    private String name;

    /**
     * 增加的防御力
     */
    private Integer buff;

    /**
     * 恢复生命
     */
    private Integer restore;

    /**
     *增加金币
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

    // 创建Id
    private Integer createUserId;
    /**
     * 是否可用
     */
    private boolean enable;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Integer getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Integer createUserId) {
        this.createUserId = createUserId;
    }

    @Override
    public String toString() {
        return "RegionMes{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", buff=" + buff +
                ", restore=" + restore +
                ", tax=" + tax +
                ", deplete=" + deplete +
                ", description='" + description + '\'' +
                '}';
    }
}
