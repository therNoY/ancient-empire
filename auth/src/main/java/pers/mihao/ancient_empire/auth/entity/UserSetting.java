package pers.mihao.ancient_empire.auth.entity;

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
    private String language;

    /**
     * 游戏内图片大小
     */
    private String imgSize;

    /**
     * 移动端风格
     */
    private String pcStyle;

    /**
     * 最大章节
     */
    private Integer maxChapter;

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

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getImgSize() {
        return imgSize;
    }

    public void setImgSize(String imgSize) {
        this.imgSize = imgSize;
    }

    public String getPcStyle() {
        return pcStyle;
    }

    public void setPcStyle(String pcStyle) {
        this.pcStyle = pcStyle;
    }

    public Integer getMaxChapter() {
        return maxChapter;
    }

    public void setMaxChapter(Integer maxChapter) {
        this.maxChapter = maxChapter;
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
