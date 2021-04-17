package pers.mihao.ancient_empire.base.vo;

import java.io.Serializable;

/**
 * 地图的基础信息
 *
 * @author mihao
 * @version 1.0
 * @date 2020\9\20 0020 11:18
 */
public class BaseMapInfoVO implements Serializable {

    private String mapId;
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
    // 地图类型
    private String type;

    public String getMapId() {
        return mapId;
    }

    public void setMapId(String mapId) {
        this.mapId = mapId;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
