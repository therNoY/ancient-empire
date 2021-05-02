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
 * @since 2021-04-26
 */
public class UserMapAttention implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    private String mapId;
    @TableId
    private Integer mapVersion;
    @TableId
    private Integer userId;

    /**
     * 地图的评分
     */
    private Integer mapStart;

    /**
     * 评论
     */
    private String mapCommend;

    /**
     * 下载时间
     */
    private LocalDateTime downlaodTime;

    /**
     * 更新时间(版本)
     */
    private LocalDateTime updateTime;

    public String getMapId() {
        return mapId;
    }

    public void setMapId(String mapId) {
        this.mapId = mapId;
    }
    public Integer getMapVersion() {
        return mapVersion;
    }

    public void setMapVersion(Integer mapVersion) {
        this.mapVersion = mapVersion;
    }
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    public Integer getMapStart() {
        return mapStart;
    }

    public void setMapStart(Integer mapStart) {
        this.mapStart = mapStart;
    }
    public String getMapCommend() {
        return mapCommend;
    }

    public void setMapCommend(String mapCommend) {
        this.mapCommend = mapCommend;
    }
    public LocalDateTime getDownlaodTime() {
        return downlaodTime;
    }

    public void setDownlaodTime(LocalDateTime downlaodTime) {
        this.downlaodTime = downlaodTime;
    }
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "UserMapAttention{" +
        "mapId=" + mapId +
        ", mapVersion=" + mapVersion +
        ", userId=" + userId +
        ", mapStart=" + mapStart +
        ", mapCommend=" + mapCommend +
        ", downlaodTime=" + downlaodTime +
        ", updateTime=" + updateTime +
        "}";
    }
}
