package com.mihao.ancient_empire.websocket;

import com.mihao.ancient_empire.common.annotation.ExecuteTime;
import com.mihao.ancient_empire.common.util.JacksonUtil;
import com.mihao.ancient_empire.constant.WSPath;
import com.mihao.ancient_empire.constant.WsMethodEnum;
import com.mihao.ancient_empire.dto.Position;
import com.mihao.ancient_empire.dto.Unit;
import com.mihao.ancient_empire.dto.ws_dto.*;
import com.mihao.ancient_empire.util.WsRespHelper;
import com.mihao.ancient_empire.websocket.service.WsActionService;
import com.mihao.ancient_empire.websocket.service.WsAttachResultService;
import com.mihao.ancient_empire.websocket.service.WsEndService;
import com.mihao.ancient_empire.websocket.service.WsMoveAreaService;
import javafx.geometry.Pos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    WsEndService wsEndService;

    /**
     * 获取移动区域
     * 如果是领主站在自己的城堡且没有移动 就显示action选项
     * @param principal
     * @param msg
     */
    @ExecuteTime
    @MessageMapping("/ws/getMoveArea")
    public void getMoveArea(Principal principal, String msg) {
        log.info("从 {} getMoveArea收到的信息", principal.getName());
        ReqUnitIndexDto unitIndexDto = JacksonUtil.jsonToBean(msg, ReqUnitIndexDto.class);
        if (unitIndexDto != null) {
            Object areas = moveAreaService.getMoveArea(principal.getName(), unitIndexDto);
            if (areas instanceof List) {
                simpMessagingTemplate.convertAndSendToUser(principal.getName(),
                        WSPath.TOPIC_USER, WsRespHelper.success(WsMethodEnum.MOVE_AREAS.getType(), areas));
            }else {
                simpMessagingTemplate.convertAndSendToUser(principal.getName(),
                        WSPath.TOPIC_USER, WsRespHelper.success(WsMethodEnum.MOVE_ACTION.getType(), areas));
            }
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
    public void getMovePath(Principal principal, String msg) {
        log.info("从 {} getMovePath 收到的信息", principal.getName());
        ReqMoveDto moveDto = JacksonUtil.jsonToBean(msg, ReqMoveDto.class);
        Map<String, Object> map = new HashMap<>();
        if (moveDto != null) {
            List<PathPosition> movePath  = null;
            Map<String, Object> actionMap = actionService.getActions(principal.getName(), moveDto, false);
            if (actionMap.get("noMove") == null || !actionMap.get("noMove").equals(true)) {
                movePath = moveAreaService.getMovePath(moveDto);
            }
            map.put("actions", actionMap);
            map.put("movePath", movePath);
            simpMessagingTemplate.convertAndSendToUser(principal.getName(),
                    WSPath.TOPIC_USER, WsRespHelper.success(WsMethodEnum.MOVE_PATH.getType(), map));
        } else {
            log.error("{} 解析错误", msg);
        }
    }


    /**
     * 获取攻击范围
     * @param principal
     * @param msg
     */
    @MessageMapping("/ws/getAttachArea")
    public void getAttachArea(Principal principal, String msg) {
        log.info("从 {} getAttachArea 收到的信息", principal.getName());
        ReqAttachAreaDto moveDto = JacksonUtil.jsonToBean(msg, ReqAttachAreaDto.class);
        List<Position> attach = actionService.getAttachArea(principal.getName(), moveDto);
        simpMessagingTemplate.convertAndSendToUser(principal.getName(),
                WSPath.TOPIC_USER, WsRespHelper.success(WsMethodEnum.ATTACH_AREA.getType(), attach));
    }

    /**
     * 获取一次攻击结果
     * @param principal
     * @param msg
     */
    @MessageMapping("/ws/getAttachResult")
    @ExecuteTime(maxTime = 50)
    public void getAttachResult(Principal principal, String msg) {
        log.info("从 {} getAttachArea 收到的信息", principal.getName());
        ReqAttachDto reqAttachDto = JacksonUtil.jsonToBean(msg, ReqAttachDto.class);
        RespAttachResultDto resultDto = attachResultService.getAttachResult(principal.getName(), reqAttachDto);
        simpMessagingTemplate.convertAndSendToUser(principal.getName(),
                WSPath.TOPIC_USER, WsRespHelper.success(WsMethodEnum.ATTACH_RESULT.getType(), resultDto));
    }

    /**
     * 获取单位 回合结束后的影响
     * @param principal
     * @param msg
     */
    @MessageMapping("/ws/getEndResult")
    @ExecuteTime(maxTime = 50)
    public void getEndResult(Principal principal, String msg) {
        log.info("从 {} getAttachArea 收到的信息", principal.getName());
        Unit Unit = JacksonUtil.jsonToBean(msg, com.mihao.ancient_empire.dto.Unit.class);
        RespEndResultDto resultDto = wsEndService.getEndResult(principal.getName(), Unit);
        simpMessagingTemplate.convertAndSendToUser(principal.getName(),
                WSPath.TOPIC_USER, WsRespHelper.success(WsMethodEnum.ATTACH_RESULT.getType(), resultDto));
    }


    // WS 测试
    @MessageMapping("/ws/test")
    public void test(Principal principal, String msg) {
        log.info("从 {} getMoveArea收到的信息{}", principal.getName(), msg);
        simpMessagingTemplate.convertAndSendToUser(principal.getName(), WSPath.TOPIC_USER, "replayMesToUser");
        simpMessagingTemplate.convertAndSend(WSPath.TOPIC_ROOM + principal.getName(), "replayMesToRoom");
    }

}
