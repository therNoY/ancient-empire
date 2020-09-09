package pers.mihao.ancient_empire.core.websocket.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.mihao.ancient_empire.base.bo.BaseSquare;
import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.entity.mongo.UserRecord;
import pers.mihao.ancient_empire.base.service.UserRecordService;
import pers.mihao.ancient_empire.base.util.AppUtil;
import pers.mihao.ancient_empire.common.constant.MqMethodEnum;
import pers.mihao.ancient_empire.common.util.MqHelper;
import pers.mihao.ancient_empire.core.dto.ReqRepairOcpDto;
import pers.mihao.ancient_empire.core.dto.RespRepairOcpResult;
import pers.mihao.ancient_empire.core.dto.SecondMoveDto;
import pers.mihao.ancient_empire.core.util.GameCoreHelper;

/**
 *
 */
@Service
public class WsOccupiedService {


    @Autowired
    UserRecordService userRecordService;
    @Autowired
    WsMoveAreaService moveAreaService;
    @Autowired
    MqHelper mqHelper;

    /**
     * 占领
     *
     * @param uuid
     * @param reqRepairOcpDto
     * @return
     */
    public RespRepairOcpResult getOccupiedResult(String uuid, ReqRepairOcpDto reqRepairOcpDto) {
        UserRecord record = userRecordService.getRecordById(uuid);
        Unit unit = AppUtil.getUnitByIndex(record, reqRepairOcpDto.getIndex());
        Integer regionIndex = GameCoreHelper.getRegionIndex(record, reqRepairOcpDto.getRegion());
        String color = record.getCurrColor();
        // 1 设置占领
        RespRepairOcpResult result = new RespRepairOcpResult();
        result.setRegionIndex(regionIndex);
        BaseSquare region = record.getGameMap().getRegions().get(regionIndex);
        region.setColor(color);
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
