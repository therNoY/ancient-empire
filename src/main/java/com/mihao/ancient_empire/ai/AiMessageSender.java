package com.mihao.ancient_empire.ai;

import com.mihao.ancient_empire.ai.constant.AiActiveEnum;
import com.mihao.ancient_empire.ai.dto.*;
import com.mihao.ancient_empire.constant.WSPath;
import com.mihao.ancient_empire.constant.WsMethodEnum;
import com.mihao.ancient_empire.dto.Position;
import com.mihao.ancient_empire.dto.Unit;
import com.mihao.ancient_empire.dto.ws_dto.PathPosition;
import com.mihao.ancient_empire.dto.ws_dto.ReqMoveDto;
import com.mihao.ancient_empire.dto.ws_dto.RespNewRoundDto;
import com.mihao.ancient_empire.dto.ws_dto.RespRepairOcpResult;
import com.mihao.ancient_empire.entity.mongo.UserRecord;
import com.mihao.ancient_empire.util.AppUtil;
import com.mihao.ancient_empire.util.WsRespHelper;
import com.mihao.ancient_empire.websocket.service.WsMoveAreaService;
import javafx.geometry.Pos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class AiMessageSender {

    Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    WsMoveAreaService moveAreaService;

    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(30);


    /**
     * 发送选择单位的结果
     *
     * @param selectUnitResult
     */
    public void sendSelectUnit(SelectUnitResult selectUnitResult) {
        addTask(() -> sendMes(AiActiveEnum.SELECT_UNIT, selectUnitResult));
    }


    /**
     * 发送单位行动指示给前端
     *
     * @param unitActionResult
     */
    public void sendMoveUnit(UserRecord record, SelectUnitResult result, UnitActionResult unitActionResult) {
        // 执行执行单位移动
        warpUnitMoveResult(record, result, unitActionResult);
        log.info("准备发送移动单位命令");
        addTask(() -> sendMes(AiActiveEnum.MOVE_UNIT, unitActionResult));
    }

    /**
     * 发送结束回合命令给前端
     * @param newRoundDto
     */
    public void sendEndTurnResult(RespNewRoundDto newRoundDto) {
        simpMessagingTemplate.convertAndSendToUser(newRoundDto.getRecord().getUuid(),
                WSPath.TOPIC_USER, WsRespHelper.success(WsMethodEnum.NEW_ROUND.type(), newRoundDto));
    }

    /**
     * 发送准备购买单位
     *
     * @param buyUnitResult
     */
    public void sendByUnit(BuyUnitResult buyUnitResult) {
        addTask(() -> sendMes(AiActiveEnum.BUY_UNIT, buyUnitResult));
    }

    /**
     * 准备延时发送
     *
     * @param endUnitResult
     * @param time
     */
    public void sendEndUnit(EndUnitResult endUnitResult, long time) {
        addTimerTask(() -> sendMes(AiActiveEnum.END, endUnitResult), time);
    }

    /**
     * 包装单位移动的信息
     *
     * @param record
     * @param result
     * @param unitActionResult
     */
    private void warpUnitMoveResult(UserRecord record, SelectUnitResult result, UnitActionResult unitActionResult) {
        int armyIndex = result.getArmyIndex();
        int unitIndex = result.getUnitIndex();
        Unit cUnit = record.getArmyList().get(armyIndex).getUnits().get(unitIndex);
        ReqMoveDto moveDto = new ReqMoveDto(unitIndex, AppUtil.getPosition(cUnit), new Position(unitActionResult.getSite()));
        moveDto.setMoveArea(unitActionResult.getMoveArea());
        List<PathPosition> pathPositions = moveAreaService.getMovePath(moveDto);
        RobotManger.getInstance(record).setPathPositions(pathPositions);
        unitActionResult.setPathPositions(pathPositions);
    }


    /**
     * 添加延迟
     *
     * @param runnable
     */
    private void addTask(Runnable runnable) {
        scheduledExecutorService.schedule(runnable, 0, TimeUnit.MILLISECONDS);
    }

    /**
     * 添加延时任务
     *
     * @param runnable
     */
    private void addTimerTask(Runnable runnable, long time) {
        scheduledExecutorService.schedule(runnable, time, TimeUnit.MILLISECONDS);
    }


    /**
     * 真实的发送消息
     *
     * @param method
     * @param activeResult
     */
    private void sendMes(AiActiveEnum method, ActiveResult activeResult) {
        simpMessagingTemplate.convertAndSendToUser(activeResult.getRecordId(),
                WSPath.TOPIC_USER, WsRespHelper.success("ai_" + method.type(), activeResult));
    }


    public void sendOccupiedResult(UserRecord record,UnitActionResult actionResult, RespRepairOcpResult result) {
        ReqMoveDto reqMoveDto = new ReqMoveDto(AppUtil.getPosition(actionResult.getSelectUnit()), (Position) actionResult.getSite(), actionResult.getMoveArea());
        List<PathPosition> pathPositions = moveAreaService.getMovePath(reqMoveDto);
        result.setPathPositions(pathPositions);
        simpMessagingTemplate.convertAndSendToUser(result.getRecordId(),
                WSPath.TOPIC_USER, WsRespHelper.success("ai_" + AiActiveEnum.OCCUPIED.type(), result));
    }
}
