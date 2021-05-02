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
public class RegionTemplateRelation implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 地形Id
     */
    @TableId
    private Integer regionId;

    /**
     * 模板Id
     */
    @TableId
    private Integer templateId;

    public Integer getRegionId() {
        return regionId;
    }

    public void setRegionId(Integer regionId) {
        this.regionId = regionId;
    }
    public Integer getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Integer templateId) {
        this.templateId = templateId;
    }

    @Override
    public String toString() {
        return "RegionTemplateRelation{" +
        "regionId=" + regionId +
        ", templateId=" + templateId +
        "}";
    }
}
