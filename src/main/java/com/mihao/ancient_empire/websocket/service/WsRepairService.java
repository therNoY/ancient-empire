package com.mihao.ancient_empire.websocket.service;

import com.mihao.ancient_empire.constant.MqMethodEnum;
import com.mihao.ancient_empire.constant.RegionEnum;
import com.mihao.ancient_empire.dto.BaseSquare;
import com.mihao.ancient_empire.dto.Unit;
import com.mihao.ancient_empire.dto.ws_dto.ReqRepairOcpDto;
import com.mihao.ancient_empire.dto.ws_dto.RespRepairOcpResult;
import com.mihao.ancient_empire.dto.ws_dto.SecondMoveDto;
import com.mihao.ancient_empire.entity.mongo.UserRecord;
import com.mihao.ancient_empire.service.UserRecordService;
import com.mihao.ancient_empire.util.AppUtil;
import com.mihao.ancient_empire.util.MqHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WsRepairService {


    @Autowired
    UserRecordService userRecordService;
    @Autowired
    WsMoveAreaService moveAreaService;
    @Autowired
    MqHelper mqHelper;

    /**
     * 修复废墟地形
     * @param uuid
     * @param reqRepairOcpDto
     * @return
     */
    public RespRepairOcpResult getRepairResult(String uuid, ReqRepairOcpDto reqRepairOcpDto) {
        UserRecord record = userRecordService.getRecordById(uuid);
        Unit unit = AppUtil.getUnitByIndex(record, reqRepairOcpDto.getIndex());
        Integer regionIndex = AppUtil.getRegionIndex(record, reqRepairOcpDto.getRegion());
        String color = record.getCurrColor();
        // 1 设置修复结果
        RespRepairOcpResult result = new RespRepairOcpResult();
        result.setRegionIndex(regionIndex);

        BaseSquare region = new BaseSquare(color, RegionEnum.TOWN.getType());
        result.setSquare(region);
        // 2 设置二次移动结果
        SecondMoveDto secondMoveDto = moveAreaService.getSecondMove(unit, record, reqRepairOcpDto);
        result.setSecondMove(secondMoveDto);

        // 3 保存mq
        result.setRecordId(uuid);
        mqHelper.sendMongoCdr(MqMethodEnum.ACTION_REPAIR_OCP, result);
        return result;

    }
}
