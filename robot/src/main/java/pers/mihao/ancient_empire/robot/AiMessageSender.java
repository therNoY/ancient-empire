package pers.mihao.ancient_empire.robot;

import pers.mihao.ancient_empire.robot.constant.AiActiveEnum;
import pers.mihao.ancient_empire.robot.dto.*;
import pers.mihao.ancient_empire.common.constant.WSPath;
import pers.mihao.ancient_empire.common.constant.WsMethodEnum;
import pers.mihao.ancient_empire.common.bo.Position;
import pers.mihao.ancient_empire.common.bo.Unit;
import pers.mihao.ancient_empire.common.bo.ws_dto.PathPosition;
import pers.mihao.ancient_empire.common.bo.ws_dto.ReqMoveDto;
import pers.mihao.ancient_empire.common.bo.ws_dto.RespNewRoundDto;
import pers.mihao.ancient_empire.common.bo.ws_dto.RespRepairOcpResult;
import pers.mihao.ancient_empire.base.entity.mongo.UserRecord;
import pers.mihao.ancient_empire.core.manger.GameCoreManger;
import com.mihao.ancient_empire.util.AppUtil;
import com.mihao.ancient_empire.util.WsRespHelper;
import pers.mihao.ancient_empire.core.websocket.service.WsMoveAreaService;
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
        GameCoreManger.getInstance(record).setPathPositions(pathPositions);
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

        simpMessagingTemplate.convertAndSendToUser(result.getRecordId(),
                WSPath.TOPIC_USER, WsRespHelper.success("ai_" + AiActiveEnum.OCCUPIED.type(), result));
    }
}
