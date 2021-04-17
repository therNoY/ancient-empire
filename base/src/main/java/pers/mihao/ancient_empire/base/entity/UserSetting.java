package pers.mihao.ancient_empire.base.entity;

import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

/**
 * <p>
 * 用户个性化设置表
 * </p>
 *
 * @author mihao
 * @since 2020-09-23
 */
public class UserSetting implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @TableId
    private Integer userId;

    /**
     * 创建地图的初始化行
     */
    private Integer mapInitRow;

    /**
     * 创建地图的初始化列
     */
    private Integer mapInitColumn;

    /**
     * 初始化地图的模板ID
     */
    private Integer mapInitTempId;

    /**
     * 初始化地图的类型
     */
    private String mapInitRegionType;

    /**
     * 是否开启优化绘图
     */
    private Boolean simpleDrawing;

    /**
     * 声音的大小
     */
    private Integer bgMusic;

    /**
     * 语言
     */
    private Integer language;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    public Integer getMapInitRow() {
        return mapInitRow;
    }

    public void setMapInitRow(Integer mapInitRow) {
        this.mapInitRow = mapInitRow;
    }
    public Integer getMapInitColumn() {
        return mapInitColumn;
    }

    public void setMapInitColumn(Integer mapInitColumn) {
        this.mapInitColumn = mapInitColumn;
    }
    public Integer getMapInitTempId() {
        return mapInitTempId;
    }

    public void setMapInitTempId(Integer mapInitTempId) {
        this.mapInitTempId = mapInitTempId;
    }
    public Boolean getSimpleDrawing() {
        return simpleDrawing;
    }

    public void setSimpleDrawing(Boolean simpleDrawing) {
        this.simpleDrawing = simpleDrawing;
    }
    public Integer getBgMusic() {
        return bgMusic;
    }

    public void setBgMusic(Integer bgMusic) {
        this.bgMusic = bgMusic;
    }

    public String getMapInitRegionType() {
        return mapInitRegionType;
    }

    public void setMapInitRegionType(String mapInitRegionType) {
        this.mapInitRegionType = mapInitRegionType;
    }

    public Integer getLanguage() {
        return language;
    }

    public void setLanguage(Integer language) {
        this.language = language;
    }

    @Override
    public String toString() {
        return "UserSetting{" +
        "userId=" + userId +
        ", mapInitRow=" + mapInitRow +
        ", mapInitColumn=" + mapInitColumn +
        ", mapInitTempId=" + mapInitTempId +
        ", simpleDrawing=" + simpleDrawing +
        ", bgMusic=" + bgMusic +
        ", language=" + language +
        "}";
    }
}
