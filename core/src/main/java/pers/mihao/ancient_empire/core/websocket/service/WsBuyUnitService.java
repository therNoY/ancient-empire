package pers.mihao.ancient_empire.core.websocket.service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.mihao.ancient_empire.base.bo.Army;
import pers.mihao.ancient_empire.base.bo.Position;
import pers.mihao.ancient_empire.base.bo.Site;
import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.dto.BuyUnitDto;
import pers.mihao.ancient_empire.base.dto.ReqBuyUnitDto;
import pers.mihao.ancient_empire.base.entity.UnitMes;
import pers.mihao.ancient_empire.base.entity.mongo.UserRecord;
import pers.mihao.ancient_empire.base.service.UnitMesService;
import pers.mihao.ancient_empire.base.service.UserRecordService;
import pers.mihao.ancient_empire.base.util.AppUtil;
import pers.mihao.ancient_empire.common.constant.MqMethodEnum;
import pers.mihao.ancient_empire.common.util.MqHelper;
import pers.mihao.ancient_empire.core.dto.ReqUnitIndexDto;
import pers.mihao.ancient_empire.core.dto.RespBuyUnitResult;
import pers.mihao.ancient_empire.core.dto.WSRespDto;
import pers.mihao.ancient_empire.core.eums.WsMethodEnum;
import pers.mihao.ancient_empire.core.util.WsRespHelper;

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
        Object obj = moveAreaService.getMoveArea(record, new ReqUnitIndexDto(), addUnit, true);

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
        return WsRespHelper.success(WsMethodEnum.BUY_UNIT.type(), result);
    }
}
