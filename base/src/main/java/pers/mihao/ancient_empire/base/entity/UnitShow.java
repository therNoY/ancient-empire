package pers.mihao.ancient_empire.base.entity;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author mihao
 * @since 2020-09-22
 */
public class UnitShow implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer unitId;

    /**
     * 单位的显示1
     */
    private String img;

    /**
     * 单位的显示2在这两个图片切换显示动态效果
     */
    private String img2;

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
    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
    public String getImg2() {
        return img2;
    }

    public void setImg2(String img2) {
        this.img2 = img2;
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
        ", img=" + img +
        ", img2=" + img2 +
        ", chooseAnimation=" + chooseAnimation +
        ", endAnimation=" + endAnimation +
        ", deadAnimation=" + deadAnimation +
        "}";
    }
}
