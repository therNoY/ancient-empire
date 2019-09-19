package com.mihao.ancient_empire.controller;

import com.mihao.ancient_empire.common.util.RespHelper;
import com.mihao.ancient_empire.common.vo.RespJson;
import com.mihao.ancient_empire.constant.MqMethodEnum;
import com.mihao.ancient_empire.dto.Army;
import com.mihao.ancient_empire.dto.Site;
import com.mihao.ancient_empire.dto.Unit;
import com.mihao.ancient_empire.dto.map_dto.ReqBuyUnitDto;
import com.mihao.ancient_empire.dto.map_dto.ReqInitMapDto;
import com.mihao.ancient_empire.dto.mongo_dto.BuyUnitDto;
import com.mihao.ancient_empire.entity.UnitMes;
import com.mihao.ancient_empire.entity.mongo.UserRecord;
import com.mihao.ancient_empire.service.UnitMesService;
import com.mihao.ancient_empire.service.UserRecordService;
import com.mihao.ancient_empire.util.AppUtil;
import com.mihao.ancient_empire.util.MqHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserRecordController {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserRecordService userRecordService;
    @Autowired
    UnitMesService unitMesService;
    @Autowired
    MqHelper mqHelper;

    /**
     * 用户选择地图 设置初始化地图 获取初始化地图的Id
     * @param reqInitMapDto
     * @param result
     * @return
     */
    @PostMapping("/record/init")
    public RespJson initMapRecord(@RequestBody @Validated ReqInitMapDto reqInitMapDto, BindingResult result) {
        String recordId = userRecordService.initMapRecord(reqInitMapDto);
        return RespHelper.successResJson(recordId);
    }

    /**
     * 建立WS 连接
     * @return
     */
    @PostMapping("/tempRecord")
    public RespJson saveTempRecord() {
        return null;
    }

    /**
     * 获取record信息
     */
    @GetMapping("/record/{uuid}")
    public RespJson getRecordById(@PathVariable("uuid") String uuid) {
        UserRecord userRecord = userRecordService.getRecordById(uuid);
        return RespHelper.successResJson(userRecord);
    }

    /**
     *  http 请求购买 单位
     * @return
     */
    @PutMapping("/record/army/buy")
    public RespJson buyArmyUnit(@RequestBody ReqBuyUnitDto buyUnitDto) {
        UserRecord record = userRecordService.getRecordById(buyUnitDto.getUuid());
        UnitMes buyUnit = unitMesService.getById(buyUnitDto.getUnitId());
        Site site = buyUnitDto.getSite();
        Army army = AppUtil.getCurrentArmy(record);
        // 判断是否超出金币
        int armyMoney = army.getMoney();
        if (armyMoney < buyUnit.getPrice()) {
            return RespHelper.errResJson(21000);
        }

        // 判断是否超出人口
        int armyPop = army.getPop();
        if (armyPop + buyUnit.getPopulation() > record.getMaxPop()) {
            return RespHelper.errResJson(21001);
        }

        int lastMoney = armyMoney - buyUnit.getPrice();
        BuyUnitDto unitDto = new BuyUnitDto();
        unitDto.setLastMoney(lastMoney);
        unitDto.setUnit(new Unit(buyUnit.getType(), site.getRow(), site.getColumn()));
        mqHelper.sendMongoCdr(MqMethodEnum.BUY_UNIT, unitDto);

        return RespHelper.successResJson();
    }
}
