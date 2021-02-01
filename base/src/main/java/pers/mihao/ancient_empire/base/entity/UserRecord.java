package pers.mihao.ancient_empire.base.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import pers.mihao.ancient_empire.base.bo.*;

/**
 * 这个DTO 属于用户存档 即是进行中的
 */
@Document
public class UserRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    private String uuid;
    // 模板ID
    private Integer templateId;
    // 人口上限
    private Integer maxPop;
    // 初始化地形图
    private GameMap gameMap;
    // 包含所有的军队信息
    private List<Army> armyList;
    // 包含的坟墓
    private List<Tomb> tombList;
    // 记录的名字
    private String recordName;
    // 创建者Id
    private Integer createUserId;
    // 创建时间
    private String createTime;
    // 当前回合数
    private Integer currentRound;
    // 当前行动的军队color
    private String currColor;
    // 当前军队的index
    private Integer currArmyIndex;
    // 当前阵容
    private Integer currCamp;
    // 当前点
    private Site currPoint;
    // 是否保存 未保存的存档最多有一个
    private boolean isUnSave;
    // 当前游戏的玩家ID
    private String currPlayer;
    // 当前单位
    private UnitInfo currUnit;
    // 当前地形
    private RegionInfo currRegion;


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

    public boolean isUnSave() {
        return isUnSave;
    }

    public void setUnSave(boolean unSave) {
        isUnSave = unSave;
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

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    public Integer getCurrArmyIndex() {
        return currArmyIndex;
    }

    public void setCurrArmyIndex(Integer currArmyIndex) {
        this.currArmyIndex = currArmyIndex;
    }

    public void setGameMap(GameMap gameMap) {
        this.gameMap = gameMap;
    }

    public List<Army> getArmyList() {
        return armyList;
    }

    public void setArmyList(List<Army> armyList) {
        this.armyList = armyList;
    }

    public String getCurrColor() {
        return currColor;
    }

    public void setCurrColor(String currColor) {
        this.currColor = currColor;
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
    }

    public Integer getCurrCamp() {
        return currCamp;
    }

    public void setCurrCamp(Integer currCamp) {
        this.currCamp = currCamp;
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
        return currUnit;
    }

    public void setCurrUnit(UnitInfo currUnit) {
        this.currUnit = currUnit;
    }

    public RegionInfo getCurrRegion() {
        return currRegion;
    }

    public void setCurrRegion(RegionInfo currRegion) {
        this.currRegion = currRegion;
    }

    @Override
    public String toString() {
        return "UserRecord{" +
            "uuid='" + uuid + '\'' +
            ", armyList=" + armyList +
            ", tombList=" + tombList +
            ", currentRound=" + currentRound +
            ", currColor='" + currColor + '\'' +
            ", currArmyIndex=" + currArmyIndex +
            ", currPoint=" + currPoint +
            ", currPlayer='" + currPlayer + '\'' +
            ", currUnit=" + currUnit +
            ", currRegion=" + currRegion +
            '}';
    }
}
