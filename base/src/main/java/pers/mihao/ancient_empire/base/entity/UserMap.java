package pers.mihao.ancient_empire.base.entity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import pers.mihao.ancient_empire.base.bo.BaseUnit;
import pers.mihao.ancient_empire.base.bo.Region;
import pers.mihao.ancient_empire.common.util.StringUtil;

/**
 * 用户地图 信息
 *
 * @author mh
 */
public class UserMap implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "uuid", type = IdType.UUID)
    private String uuid;

    /**
     * 地图版本
     */
//    @TableId
    private Integer version;

    // 单位信息
    @TableField(exist = false)
    private List<BaseUnit> units;
    @JsonIgnore
    private String unitsString;

    // 地形信息
    @TableField(exist = false)
    private List<Region> regions;
    @JsonIgnore
    private String regionString;

    // 地图的名字
    private String mapName;
    // 列
    @TableField(exist = false)
    private Integer row;
    private Integer mapRow;
    // 行
    @TableField(exist = false)
    private Integer column;
    private Integer mapColumn;
    // 创建者Id
    private Integer createUserId;
    // 创建时间
    private LocalDateTime createTime;
    // 地图类型
    private String type;
    // 是否保存 未保存的地图信息 最多只有一个
    private Integer unSave;

    // 模板ID
    private Integer templateId;

    /**
     * 模板版本
     */
    private Integer templateVersion;




    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public List<BaseUnit> getUnits() {
        return units;
    }

    public void setUnits(List<BaseUnit> units) {
        this.units = units;
        this.unitsString = null;
    }

    public List<Region> getRegions() {
        return regions;
    }

    public void setRegions(List<Region> regions) {
        this.regions = regions;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public Integer getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Integer createUserId) {
        this.createUserId = createUserId;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public Integer getUnSave() {
        return unSave;
    }

    public void setUnSave(Integer unSave) {
        this.unSave = unSave;
    }

    public Integer getRow() {
        return mapRow;
    }

    public void setRow(Integer row) {
        this.mapRow = row;
    }

    public Integer getColumn() {
        return mapColumn;
    }

    public void setColumn(Integer column) {
        this.mapColumn = column;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Integer templateId) {
        this.templateId = templateId;
    }

    public String getUnitsString() {
        if (units != null) {
            this.unitsString = JSONObject.toJSONString(units);
        }
        return unitsString;
    }

    public void setUnitsString(String unitsString) {
        this.unitsString = unitsString;
        if (StringUtil.isNotBlack(unitsString)) {
            this.units = JSONArray.parseArray(unitsString, BaseUnit.class);
        }
    }

    public String getRegionString() {
        if (regions != null) {
            this.regionString = JSONObject.toJSONString(regions);
        }
        return regionString;
    }

    public void setRegionString(String regionString) {
        this.regionString = regionString;
        if (StringUtil.isNotBlack(regionString)) {
            this.regions = JSONArray.parseArray(regionString, Region.class);
        }
    }

    public Integer getMapRow() {
        return mapRow;
    }

    public void setMapRow(Integer mapRow) {
        this.mapRow = mapRow;
    }

    public Integer getMapColumn() {
        return mapColumn;
    }

    public void setMapColumn(Integer mapColumn) {
        this.mapColumn = mapColumn;
    }

    public Integer getTemplateVersion() {
        return templateVersion;
    }

    public void setTemplateVersion(Integer templateVersion) {
        this.templateVersion = templateVersion;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}


