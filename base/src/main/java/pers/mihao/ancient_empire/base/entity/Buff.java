package pers.mihao.ancient_empire.base.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

/**
 * <p>
 * buff信息表
 * </p>
 *
 * @author mihao
 * @since 2019-08-31
 */
public class Buff implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * buff类型
     */
    private String type;

    /**
     * buff名称
     */
    private String name;

    /**
     * buff说明
     */
    private String description;

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

    @Override
    public String toString() {
        return "Buff{" +
        "id=" + id +
        ", type=" + type +
        ", name=" + name +
        ", description=" + description +
        "}";
    }
}
