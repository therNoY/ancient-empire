package com.mihao.ancient_empire.websocket.service;

import com.mihao.ancient_empire.constant.MqMethodEnum;
import com.mihao.ancient_empire.constant.WsMethodEnum;
import com.mihao.ancient_empire.dto.Army;
import com.mihao.ancient_empire.dto.Position;
import com.mihao.ancient_empire.dto.Site;
import com.mihao.ancient_empire.dto.Unit;
import com.mihao.ancient_empire.dto.map_dto.ReqBuyUnitDto;
import com.mihao.ancient_empire.dto.mongo_dto.BuyUnitDto;
import com.mihao.ancient_empire.dto.ws_dto.ReqUnitIndexDto;
import com.mihao.ancient_empire.dto.ws_dto.RespBuyUnitResult;
import com.mihao.ancient_empire.dto.ws_dto.WSRespDto;
import com.mihao.ancient_empire.entity.UnitMes;
import com.mihao.ancient_empire.entity.mongo.UserRecord;
import com.mihao.ancient_empire.service.UnitMesService;
import com.mihao.ancient_empire.service.UserRecordService;
import com.mihao.ancient_empire.util.AppUtil;
import com.mihao.ancient_empire.util.MqHelper;
import com.mihao.ancient_empire.util.WsRespHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WsBuyUnitService {

    Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    UserRecordService userRecordService;
    @Autowired
    UnitMesService unitMesService;
    @Autowired
    WsMoveAreaService moveAreaService;
    @Autowired
    MqHelper mqHelper;

    /**
     * http 请求购买 单位
     *
     * @return
     */
    public WSRespDto buyArmyUnit(String uuid, ReqBuyUnitDto buyUnitDto) {
        RespBuyUnitResult result = new RespBuyUnitResult();

        UserRecord record = userRecordService.getRecordById(uuid);
        UnitMes buyUnit = unitMesService.getByType(buyUnitDto.getType());
        Site site = buyUnitDto.getSite();
        Army army = AppUtil.getCurrentArmy(record);
        // 判断是否超出金币
        BuyUnitDto unitDto = new BuyUnitDto();
        unitDto.setUuid(uuid);
        int armyMoney = army.getMoney();
        if (armyMoney < buyUnit.getPrice()) {
            return WsRespHelper.error(21000);
        }
        unitDto.setLastMoney(armyMoney - buyUnit.getPrice());
        // 判断是否超出人口
        int armyPop = army.getPop();
        if (armyPop + buyUnit.getPopulation() > record.getMaxPop()) {
            return WsRespHelper.error(21001);
        }
        unitDto.setEndPop(armyPop + buyUnit.getPopulation());
        Unit addUnit = new Unit(buyUnit.getType(), site.getRow(), site.getColumn());
        unitDto.setUnit(addUnit);

        result.setBuyUnitDto(unitDto);
        List<Position> moveArea =null;
        Object obj = moveAreaService.getMoveArea(record, new ReqUnitIndexDto(), addUnit);

        try {
            if (obj != null && obj instanceof List) {
                moveArea = (List<Position>) obj;
            }else {
                log.error("错误应该，返回可移动地址");
            }
        } catch (Exception e) {
            log.error("", e);
        }

        result.setMoveArea(moveArea);
        mqHelper.sendMongoCdr(MqMethodEnum.BUY_UNIT, unitDto);
        return WsRespHelper.success(WsMethodEnum.BUY_UNIT.getType(), result);
    }
}
