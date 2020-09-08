package pers.mihao.ancient_empire.core.dto;

import pers.mihao.ancient_empire.base.entity.mongo.UserRecord;

import java.util.List;

/**
 * 开启新的回合 将重新获取后端同步的Record
 */
public class RespNewRoundDto {

    private UserRecord record;

    private Integer addMoney; // 新的收入

    List<LifeChange> lifeChanges; // 改变当前单位的生命值 可能增加 可能减少

    public RespNewRoundDto() {
    }

    public RespNewRoundDto(UserRecord record, Integer addMoney, List<LifeChange> lifeChanges) {
        this.record = record;
        this.addMoney = addMoney;
        this.lifeChanges = lifeChanges;
    }

    public UserRecord getRecord() {
        return record;
    }

    public void setRecord(UserRecord record) {
        this.record = record;
    }

    public Integer getAddMoney() {
        return addMoney;
    }

    public void setAddMoney(Integer addMoney) {
        this.addMoney = addMoney;
    }

    public List<LifeChange> getLifeChanges() {
        return lifeChanges;
    }

    public void setLifeChanges(List<LifeChange> lifeChanges) {
        this.lifeChanges = lifeChanges;
    }
}
