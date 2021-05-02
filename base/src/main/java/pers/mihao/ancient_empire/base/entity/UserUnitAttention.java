package pers.mihao.ancient_empire.base.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author mihao
 * @since 2021-04-29
 */
public class UserUnitAttention implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    private Integer userId;

    @TableId
    private Integer unitId;

    private String unitType;

    private Integer unitStart;

    private String unitCommend;

    private LocalDateTime downloadTime;

    private LocalDateTime updateTime;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    public Integer getUnitId() {
        return unitId;
    }

    public void setUnitId(Integer unitId) {
        this.unitId = unitId;
    }
    public Integer getUnitStart() {
        return unitStart;
    }

    public void setUnitStart(Integer unitStart) {
        this.unitStart = unitStart;
    }
    public String getUnitCommend() {
        return unitCommend;
    }

    public void setUnitCommend(String unitCommend) {
        this.unitCommend = unitCommend;
    }
    public LocalDateTime getDownloadTime() {
        return downloadTime;
    }

    public void setDownloadTime(LocalDateTime downloadTime) {
        this.downloadTime = downloadTime;
    }
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public String getUnitType() {
        return unitType;
    }

    public void setUnitType(String unitType) {
        this.unitType = unitType;
    }

    @Override
    public String toString() {
        return "UserUnitAttiention{" +
        "userId=" + userId +
        ", unitId=" + unitId +
        ", unitStart=" + unitStart +
        ", unitCommend=" + unitCommend +
        ", downloadTime=" + downloadTime +
        ", updateTime=" + updateTime +
        "}";
    }
}
