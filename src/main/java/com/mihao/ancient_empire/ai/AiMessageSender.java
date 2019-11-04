package com.mihao.ancient_empire.ai;

import com.baomidou.mybatisplus.extension.api.R;
import com.mihao.ancient_empire.ai.constant.AiActiveEnum;
import com.mihao.ancient_empire.ai.dto.ActiveResult;
import com.mihao.ancient_empire.ai.dto.SelectUnitResult;
import com.mihao.ancient_empire.ai.dto.UnitActionResult;
import com.mihao.ancient_empire.constant.WSPath;
import com.mihao.ancient_empire.dto.Position;
import com.mihao.ancient_empire.dto.Unit;
import com.mihao.ancient_empire.dto.ws_dto.PathPosition;
import com.mihao.ancient_empire.dto.ws_dto.ReqMoveDto;
import com.mihao.ancient_empire.dto.ws_dto.ReqUnitIndexDto;
import com.mihao.ancient_empire.entity.mongo.UserRecord;
import com.mihao.ancient_empire.util.AppUtil;
import com.mihao.ancient_empire.util.WsRespHelper;
import com.mihao.ancient_empire.websocket.service.WsMoveAreaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class AiMessageSender {

    Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    WsMoveAreaService moveAreaService;

    /**
     * 发送选择单位的结果
     *
     * @param selectUnitResult
     */
    public void sendSelectUnit(SelectUnitResult selectUnitResult) {
        sendMes(AiActiveEnum.SELECT_UNIT, selectUnitResult);

    }

    /**
     * 发送单位行动指示给前端
     * @param unitActionResult
     */
    public void sendMoveUnit(UserRecord record, SelectUnitResult result, UnitActionResult unitActionResult) {
        // 执行执行单位移动
        int armyIndex =  result.getArmyIndex();
        int unitIndex = result.getUnitIndex();
        Unit cUnit = record.getArmyList().get(armyIndex).getUnits().get(unitIndex);
        ReqUnitIndexDto reqUnitIndexDto = new ReqUnitIndexDto(armyIndex, unitIndex);
        Object object = moveAreaService.getMoveArea(record, reqUnitIndexDto, cUnit, false);
        List<Position> positions = (List<Position>) object;
        ReqMoveDto moveDto = new ReqMoveDto(unitIndex, AppUtil.getPosition(cUnit), new Position(unitActionResult.getSite()));
        moveDto.setPositions(positions);
        List<PathPosition> pathPositions = moveAreaService.getMovePath(moveDto);
        unitActionResult.setPathPositions(pathPositions);
        sendMes(AiActiveEnum.MOVE_UNIT, unitActionResult);
    }

    private void sendMes(AiActiveEnum method, ActiveResult activeResult) {
        simpMessagingTemplate.convertAndSendToUser(activeResult.getRecordId(),
                WSPath.TOPIC_USER, WsRespHelper.success("ai_" + method.type(), activeResult));
    }
}
