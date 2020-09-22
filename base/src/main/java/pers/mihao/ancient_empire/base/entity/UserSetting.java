package pers.mihao.ancient_empire.base.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

/**
 * <p>
 * 用户个性化设置表
 * </p>
 *
 * @author mihao
 * @since 2019-08-13
 */
public class UserSetting implements Serializable {

    private static final long serialVersionUID = 1L;

    public UserSetting() {
    }

    public UserSetting(Integer userId) {
        this.userId = userId;
    }

    public UserSetting(Integer mapInitRow, Integer mapInitColumn, Integer mapInitRegionId, String mapInitRegionType, Boolean simpleDrawing) {
        this.mapInitRow = mapInitRow;
        this.mapInitColumn = mapInitColumn;
        this.mapInitRegionId = mapInitRegionId;
        this.mapInitRegionType = mapInitRegionType;
        this.simpleDrawing = simpleDrawing;
    }

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 创建地图的初始化行
     */
    private Integer mapInitRow;

    /**
     * 创建地图的初始化列
     */
    private Integer mapInitColumn;

    // 初始化地图的Id
    @JsonIgnore
    private Integer mapInitRegionId;

    // 初始化地图的Id
    @TableField(exist = false)
    private String mapInitRegionType;

    // 是否开启优化绘图
    private Boolean simpleDrawing;

    /**
     * 声音的大小
     */
    private Integer bgMusic;

    /**
     * 语言
     */
    private Integer language;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
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

    public Integer getMapInitRegionId() {
        return mapInitRegionId;
    }

    public void setMapInitRegionId(Integer mapInitRegionId) {
        this.mapInitRegionId = mapInitRegionId;
    }

    public Boolean getSimpleDrawing() {
        return simpleDrawing;
    }

    public void setSimpleDrawing(Boolean simpleDrawing) {
        this.simpleDrawing = simpleDrawing;
    }

    public String getMapInitRegionType() {
        return mapInitRegionType;
    }

    public void setMapInitRegionType(String mapInitRegionType) {
        this.mapInitRegionType = mapInitRegionType;
    }

    public Integer getBgMusic() {
        return bgMusic;
    }

    public void setBgMusic(Integer bgMusic) {
        this.bgMusic = bgMusic;
    }

    public Integer getLanguage() {
        return language;
    }

    public void setLanguage(Integer language) {
        this.language = language;
    }

    // TODO 字段主页颜色

    // TODO 极简模式 地形图片 地形信息 地形描述 天赋描述 天赋赋予



    // 作弊模式（减伤20%） 普通模式（无） 困难模式（敌方减伤20%） 地狱模式 （敌方减伤40%）

    // AI 模式 ： 随机热血活命附庸联盟
}
