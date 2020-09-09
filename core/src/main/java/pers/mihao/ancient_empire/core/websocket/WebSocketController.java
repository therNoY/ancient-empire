package pers.mihao.ancient_empire.core.websocket;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import pers.mihao.ancient_empire.base.bo.GameMap;
import pers.mihao.ancient_empire.base.bo.Position;
import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.dto.ReqBuyUnitDto;
import pers.mihao.ancient_empire.base.util.AppUtil;
import pers.mihao.ancient_empire.common.annotation.ExecuteTime;
import pers.mihao.ancient_empire.common.util.JacksonUtil;
import pers.mihao.ancient_empire.core.RobotActive;
import pers.mihao.ancient_empire.core.dto.PathPosition;
import pers.mihao.ancient_empire.core.dto.ReqAttachAreaDto;
import pers.mihao.ancient_empire.core.dto.ReqAttachDto;
import pers.mihao.ancient_empire.core.dto.ReqMoveDto;
import pers.mihao.ancient_empire.core.dto.ReqRepairOcpDto;
import pers.mihao.ancient_empire.core.dto.ReqSummonDto;
import pers.mihao.ancient_empire.core.dto.ReqUnitIndexDto;
import pers.mihao.ancient_empire.core.dto.RespAttachResultDto;
import pers.mihao.ancient_empire.core.dto.RespEndResultDto;
import pers.mihao.ancient_empire.core.dto.RespNewRoundDto;
import pers.mihao.ancient_empire.core.dto.RespRepairOcpResult;
import pers.mihao.ancient_empire.core.dto.RespSummonResult;
import pers.mihao.ancient_empire.core.dto.WSRespDto;
import pers.mihao.ancient_empire.core.eums.ArmyEnum;
import pers.mihao.ancient_empire.core.eums.WSPath;
import pers.mihao.ancient_empire.core.eums.WsMethodEnum;
import pers.mihao.ancient_empire.core.eums.ai.AiActiveEnum;
import pers.mihao.ancient_empire.core.manger.GameCoreManger;
import pers.mihao.ancient_empire.core.util.WsRespHelper;
import pers.mihao.ancient_empire.core.websocket.service.WsActionService;
import pers.mihao.ancient_empire.core.websocket.service.WsAttachResultService;
import pers.mihao.ancient_empire.core.websocket.service.WsBuyUnitService;
import pers.mihao.ancient_empire.core.websocket.service.WsEndRoundService;
import pers.mihao.ancient_empire.core.websocket.service.WsEndService;
import pers.mihao.ancient_empire.core.websocket.service.WsMoveAreaService;
import pers.mihao.ancient_empire.core.websocket.service.WsOccupiedService;
import pers.mihao.ancient_empire.core.websocket.service.WsRepairService;
import pers.mihao.ancient_empire.core.websocket.service.WsSummonActionService;

/**
 * WS 请求的前端控住值 路由请求处理方法
 */
@Controller
public class WebSocketController {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    WsMoveAreaService moveAreaService;
    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    WsActionService actionService;
    @Autowired
    WsAttachResultService attachResultService;
    @Autowired
    WsSummonActionService summonActionService;
    @Autowired
    WsRepairService repairService;
    @Autowired
    WsOccupiedService occupiedService;
    @Autowired
    WsEndService wsEndService;
    @Autowired
    WsBuyUnitService buyUnitService;
    @Autowired
    WsEndRoundService newRoundService;

    /**
     * 获取移动区域
     * 如果是领主站在自己的城堡且没有移动 就显示action选项
     *
     * @param principal
     * @param msg
     */
    @MessageMapping("/ws/getMoveArea")
    @ExecuteTime
    public void getMoveArea(Principal principal, String msg) {
        log.info("从 {} getMoveArea收到的信息", principal.getName());
        ReqUnitIndexDto unitIndexDto = JacksonUtil.jsonToBean(msg, ReqUnitIndexDto.class);
        if (unitIndexDto != null) {
            Object areas = moveAreaService.getMoveArea(principal.getName(), unitIndexDto);
            if (areas instanceof List) {
                simpMessagingTemplate.convertAndSendToUser(principal.getName(),
                        WSPath.TOPIC_USER, WsRespHelper.success(WsMethodEnum.MOVE_AREAS.type(), areas));
            } else {
                simpMessagingTemplate.convertAndSendToUser(principal.getName(),
                        WSPath.TOPIC_USER, WsRespHelper.success(WsMethodEnum.MOVE_ACTION.type(), areas));
            }
        } else {
            log.error("{} 解析错误", msg);
        }
    }

    /**
     * 获取可进行的action
     */
    @MessageMapping("/ws/getActions")
    @ExecuteTime(maxTime = 50)
    public void getActions(Principal principal, String msg) {
        log.info("从 {} getMovePath 收到的信息", principal.getName());
        ReqMoveDto moveDto = JacksonUtil.jsonToBean(msg, ReqMoveDto.class);
        if (moveDto != null) {
            Map<String, Object> actionMap = actionService.getActions(principal.getName(), moveDto, false);
            simpMessagingTemplate.convertAndSendToUser(principal.getName(),
                    WSPath.TOPIC_USER, WsRespHelper.success(WsMethodEnum.UNIT_ACTION.type(), actionMap));
        } else {
            log.error("{} 解析错误", msg);
        }
    }

    /**
     * 获取移动路线 并且提前获取单位 行动
     *
     * @param principal
     * @param msg
     */
    @MessageMapping("/ws/getMovePath")
    @ExecuteTime(maxTime = 50)
    public void getMovePath(Principal principal, String msg) {
        log.info("从 {} getMovePath 收到的信息", principal.getName());
        ReqMoveDto moveDto = JacksonUtil.jsonToBean(msg, ReqMoveDto.class);
        Map<String, Object> map = new HashMap<>();
        if (moveDto != null) {
            List<PathPosition> movePath = null;
            Map<String, Object> actionMap = actionService.getActions(principal.getName(), moveDto, false);
            if (actionMap.get("noMove") == null || !actionMap.get("noMove").equals(true)) {
                movePath = moveAreaService.getMovePath(moveDto);
            }
            map.put("actions", actionMap);
            map.put("movePath", movePath);
            simpMessagingTemplate.convertAndSendToUser(principal.getName(),
                    WSPath.TOPIC_USER, WsRespHelper.success(WsMethodEnum.MOVE_PATH.type(), map));
        } else {
            log.error("{} 解析错误", msg);
        }
    }


    /**
     * 获取攻击范围
     *
     * @param principal
     * @param msg
     */
    @MessageMapping("/ws/getAttachArea")
    @ExecuteTime(maxTime = 50)
    public void getAttachArea(Principal principal, String msg) {
        log.info("从 {} getAttachArea 收到的信息", principal.getName());
        ReqAttachAreaDto moveDto = JacksonUtil.jsonToBean(msg, ReqAttachAreaDto.class);
        List<Position> attach = actionService.getAttachArea(principal.getName(), moveDto);
        simpMessagingTemplate.convertAndSendToUser(principal.getName(),
                WSPath.TOPIC_USER, WsRespHelper.success(WsMethodEnum.ATTACH_AREA.type(), attach));
    }

    /**
     * 获取一次攻击结果
     *
     * @param principal
     * @param msg
     */
    @MessageMapping("/ws/getAttachResult")
    @ExecuteTime
    public void getAttachResult(Principal principal, String msg) {
        log.info("从 {} getAttachArea 收到的信息", principal.getName());
        ReqAttachDto reqAttachDto = JacksonUtil.jsonToBean(msg, ReqAttachDto.class);
        RespAttachResultDto resultDto = attachResultService.getAttachResult(principal.getName(), reqAttachDto);
        simpMessagingTemplate.convertAndSendToUser(principal.getName(),
                WSPath.TOPIC_USER, WsRespHelper.success(WsMethodEnum.ATTACH_RESULT.type(), resultDto));
    }

    /**
     * 获取单位 回合结束后的影响
     *
     * @param principal
     * @param msg
     */
    @MessageMapping("/ws/getEndResult")
    @ExecuteTime(maxTime = 50)
    public void getEndResult(Principal principal, String msg) {
        log.info("从 {} getEndResult 收到的信息", principal.getName());
        Unit unit = JacksonUtil.jsonToBean(msg, Unit.class);
        RespEndResultDto resultDto = wsEndService.getEndResult(principal.getName(), unit);
        simpMessagingTemplate.convertAndSendToUser(principal.getName(),
                WSPath.TOPIC_USER, WsRespHelper.success(WsMethodEnum.END_RESULT.type(), resultDto));
    }

    /**
     * 获取召唤结果
     *
     * @param principal
     * @param msg
     */
    @MessageMapping("/ws/getSummonResult")
    @ExecuteTime(maxTime = 50)
    public void getSummonResult(Principal principal, String msg) {
        log.info("从 {} getSummonResult 收到的信息", principal.getName());
        ReqSummonDto summonDto = JacksonUtil.jsonToBean(msg, ReqSummonDto.class);
        RespSummonResult summonResult = summonActionService.getSummonResult(principal.getName(), summonDto);
        simpMessagingTemplate.convertAndSendToUser(principal.getName(),
                WSPath.TOPIC_USER, WsRespHelper.success(WsMethodEnum.SUMMON_RESULT.type(), summonResult));
    }

    /**
     * 获取修理结果
     *
     * @param principal
     * @param msg
     */
    @MessageMapping("/ws/getOccupiedResult")
    @ExecuteTime
    public void getOccupiedResult(Principal principal, String msg) {
        log.info("从 {} getSummonResult 收到的信息", principal.getName());
        ReqRepairOcpDto repairOcpDto = JacksonUtil.jsonToBean(msg, ReqRepairOcpDto.class);
        RespRepairOcpResult repairOcpResult = occupiedService.getOccupiedResult(principal.getName(), repairOcpDto);
        simpMessagingTemplate.convertAndSendToUser(principal.getName(),
                WSPath.TOPIC_USER, WsRespHelper.success(WsMethodEnum.REPAIR_RESULT.type(), repairOcpResult));
    }

    /**
     * 获取占领结果
     *
     * @param principal
     * @param msg
     */
    @MessageMapping("/ws/getRepairResult")
    @ExecuteTime
    public void getRepairResult(Principal principal, String msg) {
        log.info("从 {} getSummonResult 收到的信息", principal.getName());
        ReqRepairOcpDto reqRepairOcpDto = JacksonUtil.jsonToBean(msg, ReqRepairOcpDto.class);
        RespRepairOcpResult repairResult = repairService.getRepairResult(principal.getName(), reqRepairOcpDto);
        simpMessagingTemplate.convertAndSendToUser(principal.getName(),
                WSPath.TOPIC_USER, WsRespHelper.success(WsMethodEnum.REPAIR_RESULT.type(), repairResult));
    }

    /**
     * 请求结束回合
     *
     * @param principal
     */
    @MessageMapping("/ws/buyUnit")
    @ExecuteTime
    public void buyUnit(Principal principal, String msg) {
        log.info("从 {} buyUnit 收到的信息", principal.getName());
        ReqBuyUnitDto buyUnitDto = JacksonUtil.jsonToBean(msg, ReqBuyUnitDto.class);
        WSRespDto wsRespDto = buyUnitService.buyArmyUnit(principal.getName(), buyUnitDto);
        simpMessagingTemplate.convertAndSendToUser(principal.getName(), WSPath.TOPIC_USER, wsRespDto);
    }

    /**
     * 请求结束回合
     *
     * @param principal
     */
    @MessageMapping("/ws/getNewRound")
    @ExecuteTime
    public void getNewRound(Principal principal) {
        log.info("从 {} getSummonResult 收到的信息", principal.getName());
        RespNewRoundDto newRoundDto = newRoundService.getNewRound(principal.getName());
        GameMap map = newRoundDto.getRecord().getGameMap();
        newRoundDto.getRecord().setGameMap(null);
        simpMessagingTemplate.convertAndSendToUser(principal.getName(),
                WSPath.TOPIC_USER, WsRespHelper.success(WsMethodEnum.NEW_ROUND.type(), newRoundDto));

        // 判断新的回合是不是机器人
        if (AppUtil.getCurrentArmy(newRoundDto.getRecord()).getType().equals(ArmyEnum.AI.type())) {
            log.info("======================接下来是机器人的行动============================");
            newRoundDto.getRecord().setGameMap(map);
            RobotActive active = new RobotActive(newRoundDto.getRecord(), AiActiveEnum.SELECT_UNIT);
            GameCoreManger gameCoreManger = GameCoreManger.getInstance(newRoundDto.getRecord());
            gameCoreManger.saveRecord(newRoundDto.getRecord());
            gameCoreManger.submitActive(active);
        }

    }

    // WS 测试
    @MessageMapping("/ws/test")
    public void test(Principal principal, String msg) {
        log.info("从 {} getMoveArea收到的信息{}", principal.getName(), msg);
        simpMessagingTemplate.convertAndSendToUser(principal.getName(), WSPath.TOPIC_USER, "replayMesToUser");
        simpMessagingTemplate.convertAndSend(WSPath.TOPIC_ROOM + principal.getName(), "replayMesToRoom");
    }

}