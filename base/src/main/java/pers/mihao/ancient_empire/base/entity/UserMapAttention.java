package pers.mihao.ancient_empire.base.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;

/**
 * <p>
 * 地图下载
 * </p>
 *
 * @author mihao
 * @since 2021-04-26
 */
public class UserMapAttention implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 地图id
     */
    @TableId
    private String mapId;

    /**
     * 用户Id
     */
    @TableId
    private Integer userId;

    /**
     * 地图类型
     */
    private String mapType;


    /**
     * 地图的评分
     */
    private Integer mapStart;

    /**
     * 评论
     */
    private String mapComment;

    /**
     * 下载时间
     */
    private LocalDateTime downloadTime;

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
    public String getMapType() {
        return mapType;
    }

    public void setMapType(String mapType) {
        this.mapType = mapType;
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
    public String getMapComment() {
        return mapComment;
    }

    public void setMapComment(String mapComment) {
        this.mapComment = mapComment;
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

    @Override
    public String toString() {
        return "UserMapAttention{" +
        "mapId=" + mapId +
        ", mapVersion=" + mapType +
        ", userId=" + userId +
        ", mapStart=" + mapStart +
        ", mapCommend=" + mapComment +
        ", downlaodTime=" + downloadTime +
        ", updateTime=" + updateTime +
        "}";
    }
}
