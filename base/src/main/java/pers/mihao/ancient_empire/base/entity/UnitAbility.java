package pers.mihao.ancient_empire.base.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

/**
 * <p>
 * 能力信息表
 * </p>
 *
 * @author mihao
 * @since 2020-09-23
 */
public class UnitAbility implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 单位Id
     */
    private Integer unitId;

    /**
     * 能力Id
     */
    private Integer abilityId;

    /**
     * 使用能力的动画
     */
    private String useAbilityAnimation;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getUnitId() {
        return unitId;
    }

    public void setUnitId(Integer unitId) {
        this.unitId = unitId;
    }
    public Integer getAbilityId() {
        return abilityId;
    }

    public void setAbilityId(Integer abilityId) {
        this.abilityId = abilityId;
    }
    public String getUseAbilityAnimation() {
        return useAbilityAnimation;
    }

    public void setUseAbilityAnimation(String useAbilityAnimation) {
        this.useAbilityAnimation = useAbilityAnimation;
    }

    @Override
    public String toString() {
        return "UnitAbility{" +
        "id=" + id +
        ", unitId=" + unitId +
        ", abilityId=" + abilityId +
        ", useAbilityAnimation=" + useAbilityAnimation +
        "}";
    }
}
