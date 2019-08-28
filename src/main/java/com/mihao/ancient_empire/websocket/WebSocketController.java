package com.mihao.ancient_empire.websocket;

import com.mihao.ancient_empire.common.util.JacksonUtil;
import com.mihao.ancient_empire.common.util.RedisHelper;
import com.mihao.ancient_empire.constant.WSPath;
import com.mihao.ancient_empire.dto.Position;
import com.mihao.ancient_empire.dto.Region;
import com.mihao.ancient_empire.dto.ws_dto.ReqMoveDto;
import com.mihao.ancient_empire.dto.ws_dto.ReqUnitIndexDto;
import com.mihao.ancient_empire.util.WsRespHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.jws.WebService;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
public class WebSocketController {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    WebSocketService webSocketService;
    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    /**
     * 获取移动区域
     * @param principal
     * @param msg
     */
    @MessageMapping("/ws/getMoveArea")
    public void getMoveArea(Principal principal, String msg) {
        log.info("从 {} getMoveArea收到的信息{}",principal.getName(), msg);
        ReqUnitIndexDto unitIndexDto = JacksonUtil.jsonToBean(msg, ReqUnitIndexDto.class);
        if (unitIndexDto != null) {
            List<Position> areas = webSocketService.getMoveArea(principal.getName(), unitIndexDto);
            simpMessagingTemplate.convertAndSendToUser(principal.getName(),
                    WSPath.TOPIC_USER, WsRespHelper.init("moveAreas", areas));
        }else {
            log.error("{} 解析错误", msg);
        }
    }

    /**
     * 获取移动路线
     * @param principal
     * @param msg
     */
    @MessageMapping("/ws/getMovePath")
    public void getMovePath(Principal principal, String msg) {
        log.info("从 {} getMovePath 收到的信息{}",principal.getName(), msg);
        ReqMoveDto moveDto = JacksonUtil.jsonToBean(msg, ReqMoveDto.class);
        if (moveDto != null) {
            List<Position> movePath = webSocketService.getMovePath(moveDto);
            simpMessagingTemplate.convertAndSendToUser(principal.getName(),
                    WSPath.TOPIC_USER, WsRespHelper.init("movePath", movePath));
        }else {
            log.error("{} 解析错误", msg);
        }
    }

    @MessageMapping("/ws/test")
    public void test(Principal principal, String msg) {
        log.info("从 {} getMoveArea收到的信息{}",principal.getName(), msg);
        simpMessagingTemplate.convertAndSendToUser(principal.getName(), WSPath.TOPIC_USER, "replayMesToUser");
        simpMessagingTemplate.convertAndSend(WSPath.TOPIC_ROOM + principal.getName(), "replayMesToRoom");
    }

}
