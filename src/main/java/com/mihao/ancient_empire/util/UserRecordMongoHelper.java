package com.mihao.ancient_empire.util;

import com.mihao.ancient_empire.constant.StateEnum;
import com.mihao.ancient_empire.constant.UnitEnum;
import com.mihao.ancient_empire.dto.Army;
import com.mihao.ancient_empire.dto.InitMap;
import com.mihao.ancient_empire.dto.Position;
import com.mihao.ancient_empire.dto.Unit;
import com.mihao.ancient_empire.dto.mongo_dto.SummonDto;
import com.mihao.ancient_empire.dto.ws_dto.LifeChange;
import com.mihao.ancient_empire.dto.ws_dto.RespEndResultDto;
import com.mihao.ancient_empire.entity.mongo.UserRecord;
import com.mihao.ancient_empire.service.UserRecordService;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 更新简单属性 逐个更新
 * 大的属性 删除插入
 */
@Component
public class UserRecordMongoHelper {

    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    UserRecordService userRecordService;

    /**
     * 给记录增加一个墓碑
     */
    public long addRecordTomb(String id, Position tomb) {
        Query query = Query.query(Criteria.where("_id").is(id));
        Update update = new Update();
        update.addToSet("tomb", tomb);
        UpdateResult result = mongoTemplate.updateFirst(query, update, UserRecord.class);
        return result.getMatchedCount();
    }

    /**
     * 移除记录的一个墓碑
     */
    public long removeRecordTomb(String id, Position tomb) {
        Query query = Query.query(Criteria.where("_id").is(id));
        Update update = new Update().pull("tomb", tomb);
        UpdateResult result = mongoTemplate.updateFirst(query, update, UserRecord.class);
        return result.getMatchedCount();
    }

    /**
     * 修改 记录的Army
     */
    public long updateArmy(String id, List<Army> armyList) {
        Query query = Query.query(Criteria.where("_id").is(id));
        Update update = new Update().set("armyList", armyList);
        UpdateResult result = mongoTemplate.updateFirst(query, update, UserRecord.class);
        return result.getMatchedCount();
    }

    /**
     * 修改 记录的Army
     */
    @Transactional
    public long updateRecord(String id, UserRecord record) {
        // 先删除 在添加
        Query query = Query.query(Criteria.where("_id").is(id));
        mongoTemplate.remove(query, UserRecord.class);
        mongoTemplate.save(record, "userRecord");
        // 添加
        return 1;
    }

    /**
     * 更新当前颜色
     *
     * @param id
     * @param color
     */
    public void updateColor(String id, String color) {
        Query query = Query.query(Criteria.where("_id").is(id));
        Update update = new Update();
        update.set("currColor", color);
        mongoTemplate.updateFirst(query, update, UserRecord.class);
    }

    /**
     * 更新回合数
     *
     * @param id
     * @param currentRound
     */
    public void updateRound(String id, Integer currentRound) {
        // 先删除 在添加
        Query query = Query.query(Criteria.where("_id").is(id));
        Update update = new Update();
        update.set("currentRound", currentRound);
        mongoTemplate.updateFirst(query, update, UserRecord.class);
    }

    /**
     * 更新回合
     *
     * @param id
     * @param currentRound
     */
    public void updateRound(String id, Integer currentRound, String color) {
        // 先删除 在添加
        Query query = Query.query(Criteria.where("_id").is(id));
        Update update = new Update();
        update.set("currentRound", currentRound);
        update.set("currColor", color);
        mongoTemplate.updateFirst(query, update, UserRecord.class);
    }


    /**
     * 修改 记录的 Map
     */
    public long updateMap(String id, InitMap map) {
        // 先删除 在添加
        Query query = Query.query(Criteria.where("_id").is(id));
        Update update = new Update().set("initMap", map);
        UpdateResult result = mongoTemplate.updateFirst(query, update, UserRecord.class);
        // 添加
        return result.getMatchedCount();
    }

    @Transactional
    public void handleSummon(SummonDto summonDto) {
        UserRecord record = userRecordService.getRecordById(summonDto.getUuid());
        Army army = AppUtil.getCurrentArmy(record);
        Unit unit = army.getUnits().get(summonDto.getIndex());
        if (summonDto.getLevelDto().getLeaveUp()) {
            unit.setLevel(unit.getLevel() + 1);
        }
        unit.setExperience(summonDto.getLevelDto().getEndExperience());

        Unit newUnit = new Unit(UnitEnum.BONE.getType(), summonDto.getTomb().getRow(), summonDto.getTomb().getColumn());
        newUnit.setLevel(unit.getLevel());
        army.getUnits().add(newUnit);

        Query query = Query.query(Criteria.where("_id").is(summonDto.getUuid()));
        Update update = new Update().set("armyList", record.getArmyList());
        update.pull("tomb", summonDto.getTomb());
        mongoTemplate.updateFirst(query, update, UserRecord.class);
    }

    /**
     * 处理结束的result
     *
     * @param endResultDto
     */
    public void handleEnd(RespEndResultDto endResultDto) {
        UserRecord record = userRecordService.getRecordById(endResultDto.getUuid());
        Map<Integer, List<LifeChange>> map = endResultDto.getLifeChanges();

        // 设置移动单位影响的单位
        if (map != null) {
            for (Map.Entry<Integer, List<LifeChange>> entry : map.entrySet()) {
                if (entry.getValue() != null) {
                    Army army = record.getArmyList().get(entry.getKey());
                    List<Unit> units = army.getUnits();
                    for (LifeChange lifeChange : entry.getValue()) {
                        if (lifeChange != null) {
                            Unit unit = units.get(lifeChange.getIndex());
                            if (lifeChange.getLastLife() != null) {
                                unit.setLife(lifeChange.getLastLife());
                            }
                            if (lifeChange.getState() != null) {
                                if (lifeChange.getState().equals(StateEnum.NORMAL.getType())) {
                                    unit.setStatus(null);
                                } else {
                                    unit.setStatus(lifeChange.getState());
                                }
                            }
                        }
                    }
                }

            }
        }

        // 设置异动单位自己的移动情况
        for (Unit unit : AppUtil.getCurrentArmy(record).getUnits()) {
            if (unit.getId().equals(endResultDto.getUnitId())) {
                unit.setRow(endResultDto.getRow());
                unit.setColumn(endResultDto.getColumn());
            }
        }

        // 保存db
        Query query = Query.query(Criteria.where("_id").is(endResultDto.getUuid()));
        Update update = new Update().set("armyList", record.getArmyList());
        mongoTemplate.updateFirst(query, update, UserRecord.class);
    }
}
