package pers.mihao.ancient_empire.base.entity;

import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author mihao
 * @since 2020-09-23
 */
public class UnitTemplateRelation implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 单位ID
     */
    @TableId
    private Integer unitId;

    /**
     * 模板ID
     */
    @TableId
    private Integer tempId;



    public Integer getUnitId() {
        return unitId;
    }

    public void setUnitId(Integer unitId) {
        this.unitId = unitId;
    }
    public Integer getTempId() {
        return tempId;
    }

    public void setTempId(Integer tempId) {
        this.tempId = tempId;
    }


    @Override
    public String toString() {
        return "UnitTemplateRelation{" +
        "unitId=" + unitId +
        ", tempId=" + tempId +
        "}";
    }
}
