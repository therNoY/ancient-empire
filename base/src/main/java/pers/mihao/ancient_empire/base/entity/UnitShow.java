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
public class UnitShow implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    private Integer unitId;


    /**
     * 选择单位的动画
     */
    private String chooseAnimation;

    /**
     * 结束回合的动画
     */
    private String endAnimation;

    /**
     * 死亡的动画
     */
    private String deadAnimation;

    public Integer getUnitId() {
        return unitId;
    }

    public void setUnitId(Integer unitId) {
        this.unitId = unitId;
    }

    public String getChooseAnimation() {
        return chooseAnimation;
    }

    public void setChooseAnimation(String chooseAnimation) {
        this.chooseAnimation = chooseAnimation;
    }
    public String getEndAnimation() {
        return endAnimation;
    }

    public void setEndAnimation(String endAnimation) {
        this.endAnimation = endAnimation;
    }
    public String getDeadAnimation() {
        return deadAnimation;
    }

    public void setDeadAnimation(String deadAnimation) {
        this.deadAnimation = deadAnimation;
    }

    @Override
    public String toString() {
        return "UnitShow{" +
        "unitId=" + unitId +
        ", chooseAnimation=" + chooseAnimation +
        ", endAnimation=" + endAnimation +
        ", deadAnimation=" + deadAnimation +
        "}";
    }
}
