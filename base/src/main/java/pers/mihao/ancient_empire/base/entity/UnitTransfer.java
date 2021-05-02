package pers.mihao.ancient_empire.base.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

/**
 * <p>
 * 转职可以转多个
 * </p>
 *
 * @author mihao
 * @since 2020-10-24
 */
public class UnitTransfer implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 转职之前的单位
     */
    @TableId
    private Integer unitId;

    /**
     * 转职后的单位Id
     */
    @TableId
    private Integer transferUnitId;

    /**
     * 转职的动画
     */
    private String transferAnimation;

    /**
     * 是否默认
     */
    private Integer order;

    /**
     * 是否启动
     */
    private Integer use;

    public Integer getUnitId() {
        return unitId;
    }

    public void setUnitId(Integer unitId) {
        this.unitId = unitId;
    }
    public Integer getTransferUnitId() {
        return transferUnitId;
    }

    public void setTransferUnitId(Integer transferUnitId) {
        this.transferUnitId = transferUnitId;
    }
    public String getTransferAnimation() {
        return transferAnimation;
    }

    public void setTransferAnimation(String transferAnimation) {
        this.transferAnimation = transferAnimation;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Integer getUse() {
        return use;
    }

    public void setUse(Integer use) {
        this.use = use;
    }

    @Override
    public String toString() {
        return "UnitTransfer{" +
        "unitId=" + unitId +
        ", transferUnitId=" + transferUnitId +
        ", transferAnimation=" + transferAnimation +
        ", use=" + use +
        "}";
    }
}
