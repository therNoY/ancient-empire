package pers.mihao.ancient_empire.base.entity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.util.Assert;
import pers.mihao.ancient_empire.base.bo.*;
import pers.mihao.ancient_empire.base.service.RegionMesService;
import pers.mihao.ancient_empire.base.service.UnitMesService;
import pers.mihao.ancient_empire.common.util.ApplicationContextHolder;
import pers.mihao.ancient_empire.common.util.BeanUtil;
import pers.mihao.ancient_empire.common.util.StringUtil;

/**
 * 这个DTO 属于用户存档 即是进行中的
 *
 * @author mh
 */
public class UserRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "uuid", type = IdType.UUID)
    private String uuid;
    /**
     * 取得地图Id
     **/
    private String mapId;
    // 模板ID
    private Integer templateId;
    // 人口上限
    private Integer maxPop;

    // 初始化地形图
    @TableField(exist = false)
    private GameMap gameMap;
    @JsonIgnore
    private String gameMapString;

    // 包含所有的军队信息
    @TableField(exist = false)
    private List<Army> armyList;
    @JsonIgnore
    private String armyListString;

    /**
     * 游戏开始的类型
     */
    private String type;

    // 包含的坟墓
    @TableField(exist = false)
    private List<Tomb> tombList;
    @JsonIgnore
    private String tombListString;

    // 记录的名字
    private String recordName;
    // 创建者Id
    private Integer createUserId;
    // 创建时间
    private LocalDateTime createTime;
    // 当前回合数
    private Integer currentRound;
    // 当前军队的index
    private Integer currArmyIndex;

    // 是否保存 未保存的存档最多有一个
    private Integer unSave;
    // 当前游戏的玩家ID
    private String currPlayer;
    // 当前单位
    @TableField(exist = false)
    private UnitInfo currUnit;
    @JsonIgnore
    private String currUnitUuid;

    // 当前地形
    @TableField(exist = false)
    private RegionInfo currRegion;
    @JsonIgnore
    private Integer currRegionIndex;

    // 当前点
    @TableField(exist = false)
    private Site currPoint;
    @JsonIgnore
    private Integer currPointRow;
    @JsonIgnore
    private Integer currPointColumn;

    public Integer getMaxPop() {
        return maxPop;
    }

    public void setMaxPop(Integer maxPop) {
        this.maxPop = maxPop;
    }

    public String getRecordName() {
        return recordName;
    }

    public void setRecordName(String recordName) {
        this.recordName = recordName;
    }

    public void setUnSave(Integer unSave) {
        this.unSave = unSave;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    public GameMap getGameMap() {
        return gameMap;
    }

    public void setGameMap(GameMap gameMap) {
        this.gameMap = gameMap;
    }

    public Integer getCurrArmyIndex() {
        return currArmyIndex;
    }

    public void setCurrArmyIndex(Integer currArmyIndex) {
        this.currArmyIndex = currArmyIndex;
    }


    public List<Army> getArmyList() {
        return armyList;
    }

    public void setArmyList(List<Army> armyList) {
        this.armyList = armyList;
        this.armyListString = null;
    }

    public String getCurrColor() {
        if (currArmyIndex == null) {
            return null;
        }
        return getArmyList().get(currArmyIndex).getColor();
    }

    public Integer getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(Integer currentRound) {
        this.currentRound = currentRound;
    }

    public List<Tomb> getTombList() {
        if (tombList == null) {
            tombList = new ArrayList<>();
        }
        return tombList;
    }

    public void setTombList(List<Tomb> tombList) {
        this.tombList = tombList;
        this.tombListString = null;
    }

    public Integer getCurrCamp() {
        if (currArmyIndex == null) {
            return null;
        }
        return getArmyList().get(currArmyIndex).getCamp();
    }

    public Integer getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Integer templateId) {
        this.templateId = templateId;
    }

    public Site getCurrPoint() {
        return currPoint;
    }

    public void setCurrPoint(Site currPoint) {
        this.currPoint = currPoint;
    }

    public String getCurrPlayer() {
        return currPlayer;
    }

    public void setCurrPlayer(String currPlayer) {
        this.currPlayer = currPlayer;
    }

    public UnitInfo getCurrUnit() {
        if (StringUtil.isNotBlack(currUnitUuid) && currUnit == null) {
            for (int i = 0; i < armyList.size(); i++) {
                Army army = armyList.get(i);
                for (int j = 0; j < army.getUnits().size(); j++) {
                    Unit unit = army.getUnits().get(j);
                    if (unit.getId().equals(currUnitUuid)) {
                        currUnit = ApplicationContextHolder.getBean(UnitMesService.class)
                            .getUnitInfo(unit.getTypeId(), unit.getLevel());
                        BeanUtil.copyValueFromParent(unit, currUnit);
                        currUnit.setColor(army.getColor());
                        currUnit.setArmyIndex(i);
                        currUnit.setUnitIndex(j);
                        break;
                    }
                }
            }
        }
        return currUnit;
    }

    public void setCurrUnit(UnitInfo currUnit) {
        this.currUnit = currUnit;
        if (this.currUnit != null) {
            this.currUnitUuid = this.currUnit.getId();
        }
    }

    public RegionInfo getCurrRegion() {
        if (currRegionIndex != null && currRegion == null) {
            Region region = getGameMap().getRegions().get(currRegionIndex);
            RegionMesService regionMesService = ApplicationContextHolder.getBean(RegionMesService.class);
            RegionMes regionMes = regionMesService.getRegionByTypeFromLocalCatch(region.getType());
            RegionInfo regionInfo = BeanUtil.copyValueFromParent(regionMes, RegionInfo.class);
            regionInfo.setColor(region.getColor());
            int row = (currRegionIndex + 1) / gameMap.getColumn() + 1;
            int column = (currRegionIndex + 1) % gameMap.getColumn();
            regionInfo.setRow(row);
            regionInfo.setColumn(column);
            regionInfo.setIndex(currRegionIndex);
            currRegion = regionInfo;
        }
        return currRegion;
    }

    public void setCurrRegion(RegionInfo currRegion) {
        this.currRegion = currRegion;
    }

    public String getMapId() {
        return mapId;
    }

    public void setMapId(String mapId) {
        this.mapId = mapId;
    }

    //

    public String getGameMapString() {
        if (gameMap != null) {
            this.gameMapString = JSONObject.toJSONString(gameMap);
        }
        return gameMapString;

    }

    public void setGameMapString(String gameMapString) {
        this.gameMapString = gameMapString;
        if (StringUtil.isNotBlack(gameMapString)) {
            this.gameMap = JSONObject.parseObject(gameMapString, GameMap.class);
        }
    }

    public String getArmyListString() {
        if (armyList != null) {
            this.armyListString = JSONObject.toJSONString(armyList);
        }
        return armyListString;
    }

    public void setArmyListString(String armyListString) {
        this.armyListString = armyListString;
        if (StringUtil.isNotBlack(armyListString)) {
            this.armyList = JSONArray.parseArray(armyListString, Army.class);
        }
    }

    public String getTombListString() {
        if (tombList != null) {
            this.tombListString = JSONObject.toJSONString(tombList);
        }
        return tombListString;
    }

    public void setTombListString(String tombListString) {
        this.tombListString = tombListString;
        if (StringUtil.isNotBlack(tombListString)) {
            this.tombList = JSONArray.parseArray(tombListString, Tomb.class);
        }
    }

    public Integer getUnSave() {
        return unSave;
    }

    public String getCurrUnitUuid() {
        if (currUnit != null) {
            this.currUnitUuid = currUnit.getId();
        }
        return currUnitUuid;
    }

    public void setCurrUnitUuid(String currUnitUuid) {
        this.currUnitUuid = currUnitUuid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getCurrRegionIndex() {
        if (currRegion != null) {
            this.currRegionIndex = currRegion.getIndex();
            Assert.notNull(this.currRegionIndex, "不能为空");
        }
        return currRegionIndex;
    }

    public void setCurrRegionIndex(Integer currRegionIndex) {
        this.currRegionIndex = currRegionIndex;
    }

    public Integer getCurrPointRow() {
        return currPoint.getRow();
    }

    public void setCurrPointRow(Integer currPointRow) {
        this.currPointRow = currPointRow;
        if (this.currPoint == null) {
            this.currPoint = new Site();
        }
        this.currPoint.setRow(currPointRow);
    }

    public Integer getCurrPointColumn() {
        return currPoint.getColumn();
    }

    public void setCurrPointColumn(Integer currPointColumn) {
        this.currPointColumn = currPointColumn;
        if (this.currPoint == null) {
            this.currPoint = new Site();
        }
        this.currPoint.setColumn(currPointColumn);
    }

    @Override
    public String toString() {
        return "UserRecord{" +
            "uuid='" + uuid + '\'' +
            ", armyList=" + armyList +
            ", tombList=" + tombList +
            ", currentRound=" + currentRound +
            ", currColor='" + getCurrColor() + '\'' +
            ", currArmyIndex=" + currArmyIndex +
            ", currPoint=" + currPoint +
            ", currPlayer='" + currPlayer + '\'' +
            ", currUnit=" + currUnit +
            ", currRegion=" + currRegion +
            '}';
    }
}
