package pers.mihao.ancient_empire.base.entity;

import pers.mihao.ancient_empire.base.bo.BaseSquare;
import pers.mihao.ancient_empire.base.bo.BaseUnit;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * 用户地图 信息
 */
@Document
public class UserMap implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String uuid;
    // 单位信息
    private List<BaseUnit> units;
    // 地形信息
    private List<BaseSquare> regions;
    // 地图的名字
    private String mapName;
    // 列
    private Integer row;
    // 行
    private Integer column;
    // 创建者Id
    private Integer createUserId;
    // 创建时间
    private String createTime;
    // 引用的人的Id
    private List<Integer> referenceUserId;
    // 地图类型
    private String type;
    // 是否保存 未保存的地图信息 最多只有一个
    private boolean unSave;

    // 模板ID
    private Integer templateId;


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
    }

    public List<BaseSquare> getRegions() {
        return regions;
    }

    public void setRegions(List<BaseSquare> regions) {
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

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public List<Integer> getReferenceUserId() {
        return referenceUserId;
    }

    public void setReferenceUserId(List<Integer> referenceUserId) {
        this.referenceUserId = referenceUserId;
    }

    public boolean isUnSave() {
        return unSave;
    }

    public void setUnSave(boolean unSave) {
        this.unSave = unSave;
    }

    public Integer getRow() {
        return row;
    }

    public void setRow(Integer row) {
        this.row = row;
    }

    public Integer getColumn() {
        return column;
    }

    public void setColumn(Integer column) {
        this.column = column;
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
}
