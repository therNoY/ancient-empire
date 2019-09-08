package com.mihao.ancient_empire.entity.mongo;

import com.mihao.ancient_empire.dto.Army;
import com.mihao.ancient_empire.dto.InitMap;
import com.mihao.ancient_empire.dto.Position;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;

/**
 * 这个DTO 属于用户存档 即是进行中的
 */
@Document
public class UserRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    private String uuid;
    // 人口上限
    private Integer maxPop;
    //初始化地形图
    private InitMap initMap;
    //包含所有的军队信息
    private List<Army> armyList;
    // 包含的坟墓
    private List<Position> tomb;
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
    // 当前阵容
    private Integer currCamp;
    // 是否保存 未保存的存档最多有一个
    private boolean isUnSave;

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

    public InitMap getInitMap() {
        return initMap;
    }

    public void setInitMap(InitMap initMap) {
        this.initMap = initMap;
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

    public List<Position> getTomb() {
        return tomb;
    }

    public void setTomb(List<Position> tomb) {
        this.tomb = tomb;
    }

    public Integer getCurrCamp() {
        return currCamp;
    }

    public void setCurrCamp(Integer currCamp) {
        this.currCamp = currCamp;
    }
}
