package com.mihao.ancient_empire.websocket;

import com.mihao.ancient_empire.common.annotation.ExecuteTime;
import com.mihao.ancient_empire.common.util.JacksonUtil;
import com.mihao.ancient_empire.constant.WSPath;
import com.mihao.ancient_empire.constant.WsMethodEnum;
import com.mihao.ancient_empire.dto.Position;
import com.mihao.ancient_empire.dto.ws_dto.PathPosition;
import com.mihao.ancient_empire.dto.ws_dto.ReqMoveDto;
import com.mihao.ancient_empire.dto.ws_dto.ReqUnitIndexDto;
import com.mihao.ancient_empire.dto.ws_dto.RespAction;
import com.mihao.ancient_empire.util.WsRespHelper;
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

@Controller
public class WebSocketController {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    WebSocketService webSocketService;
    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    /**
     * 获取移动区域
     *
     * @param principal
     * @param msg
     */
    @ExecuteTime(maxTime = 200)
    @MessageMapping("/ws/getMoveArea")
    public void getMoveArea(Principal principal, String msg) {
        log.info("从 {} getMoveArea收到的信息", principal.getName());
        ReqUnitIndexDto unitIndexDto = JacksonUtil.jsonToBean(msg, ReqUnitIndexDto.class);
        if (unitIndexDto != null) {
            List<Position> areas = webSocketService.getMoveArea(principal.getName(), unitIndexDto);
            simpMessagingTemplate.convertAndSendToUser(principal.getName(),
                    WSPath.TOPIC_USER, WsRespHelper.success(WsMethodEnum.MOVE_AREAS.getType(), areas));
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
    @ExecuteTime(maxTime = 50)
    @MessageMapping("/ws/getMovePath")
    public void getMovePath(Principal principal, String msg) {
        log.info("从 {} getMovePath 收到的信息", principal.getName());
        ReqMoveDto moveDto = JacksonUtil.jsonToBean(msg, ReqMoveDto.class);
        if (moveDto != null) {
            List<RespAction> actions = webSocketService.getActions(principal.getName(), moveDto);
            List<PathPosition> movePath = webSocketService.getMovePath(moveDto);
            Map<String, Object> map = new HashMap<>();
            map.put("actions", actions);
            map.put("movePath", movePath);
            simpMessagingTemplate.convertAndSendToUser(principal.getName(),
                    WSPath.TOPIC_USER, WsRespHelper.success(WsMethodEnum.MOVE_PATH.getType(), map));
        } else {
            log.error("{} 解析错误", msg);
        }
    }

    @MessageMapping("/ws/test")
    public void test(Principal principal, String msg) {
        log.info("从 {} getMoveArea收到的信息{}", principal.getName(), msg);
        simpMessagingTemplate.convertAndSendToUser(principal.getName(), WSPath.TOPIC_USER, "replayMesToUser");
        simpMessagingTemplate.convertAndSend(WSPath.TOPIC_ROOM + principal.getName(), "replayMesToRoom");
    }

}
