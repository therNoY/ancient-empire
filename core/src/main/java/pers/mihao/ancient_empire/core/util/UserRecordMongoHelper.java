package pers.mihao.ancient_empire.core.util;

import com.mongodb.client.result.UpdateResult;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pers.mihao.ancient_empire.base.bo.Army;
import pers.mihao.ancient_empire.base.bo.BaseSquare;
import pers.mihao.ancient_empire.base.bo.GameMap;
import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.dto.BuyUnitDto;
import pers.mihao.ancient_empire.base.dto.SummonDto;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.base.enums.StateEnum;
import pers.mihao.ancient_empire.base.service.UserRecordService;
import pers.mihao.ancient_empire.base.util.AppUtil;
import pers.mihao.ancient_empire.core.dto.LifeChange;
import pers.mihao.ancient_empire.core.dto.RespEndResultDto;
import pers.mihao.ancient_empire.core.dto.RespRepairOcpResult;

/**
 * 更新简单属性 逐个更新 大的属性 删除插入
 */
@Component
public class UserRecordMongoHelper {

    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    UserRecordService userRecordService;

    /**
     * 修改 记录的Army
     */
    public long updateArmyAndTomb(UserRecord record) {
        Query query = Query.query(Criteria.where("_id").is(record.getUuid()));
        Update update = new Update().set("armyList", record.getArmyList());
        update.pull("tomb", record.getTomb());
        UpdateResult result = mongoTemplate.updateFirst(query, update, UserRecord.class);
        return result.getMatchedCount();
    }

    /**
     * 修改 记录的Army
     */
    @Transactional
    public long updateRecord(UserRecord record) {
        // 先删除 在添加
        Query query = Query.query(Criteria.where("_id").is(record.getUuid()));
        mongoTemplate.remove(query, UserRecord.class);
        mongoTemplate.save(record, "userRecord");
        // 添加
        return 1;
    }

    /**
     * 修改 记录的 Map
     */
    public long updateMap(String id, GameMap map) {
        // 先删除 在添加
        Query query = Query.query(Criteria.where("_id").is(id));
        Update update = new Update().set("initMap", map);
        UpdateResult result = mongoTemplate.updateFirst(query, update, UserRecord.class);
        // 添加
        return result.getMatchedCount();
    }


    /**
     * 处理召唤后的result
     *
     * @param summonDto
     */
    @Transactional
    public void handleSummon(SummonDto summonDto) {
        UserRecord record = userRecordService.getRecordById(summonDto.getUuid());
        Army army = AppUtil.getCurrentArmy(record);
        Unit unit = army.getUnits().get(summonDto.getIndex());
        if (summonDto.getLevelDto().getLeaveUp()) {
            unit.setLevel(unit.getLevel() + 1);
        }
        unit.setExperience(summonDto.getLevelDto().getEndExperience());

        army.getUnits().add(summonDto.getNewUnit());

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
                                if (lifeChange.getState().equals(StateEnum.NORMAL.type())) {
                                    unit.setStatus(null);
                                } else {
                                    unit.setStatus(lifeChange.getState());
                                    unit.setStatusPresenceNum(3);
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

    /**
     * 处理修复后的result
     *
     * @param repairResult
     */
    public void handleRepairOcp(RespRepairOcpResult repairResult) {
        UserRecord record = userRecordService.getRecordById(repairResult.getRecordId());
        List<BaseSquare> regions = record.getGameMap().getRegions();
        BaseSquare square = repairResult.getSquare();
        regions.get(repairResult.getRegionIndex()).setColor(square.getColor());
        regions.get(repairResult.getRegionIndex()).setType(square.getType());
        updateMap(repairResult.getRecordId(), record.getGameMap());
    }

    public void handleBuyUnit(BuyUnitDto buyUnitDto) {
        UserRecord record = userRecordService.getRecordById(buyUnitDto.getUuid());
        List<Army> armies = record.getArmyList();
        for (Army army : armies) {
            if (army.getColor().equals(record.getCurrColor())) {
                army.setMoney(buyUnitDto.getLastMoney());
                army.setPop(buyUnitDto.getEndPop());
                army.getUnits().add(buyUnitDto.getUnit());
                break;
            }
        }
        Query query = Query.query(Criteria.where("_id").is(buyUnitDto.getUuid()));
        Update update = new Update().set("armyList", armies);
        mongoTemplate.updateFirst(query, update, UserRecord.class);
    }


    public void endRound(UserRecord record) {
        Query query = Query.query(Criteria.where("_id").is(record.getUuid()));
        Update update = new Update().set("armyList", record.getArmyList());
        update.set("tomb", record.getTomb());
        update.set("currentRound", record.getCurrentRound());
        update.set("currColor", record.getCurrColor());
        update.set("currCamp", record.getCurrCamp());
        mongoTemplate.updateFirst(query, update, UserRecord.class);
    }
}
