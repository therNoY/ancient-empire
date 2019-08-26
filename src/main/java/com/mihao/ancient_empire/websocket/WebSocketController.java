package com.mihao.ancient_empire.websocket;

import com.mihao.ancient_empire.common.util.JacksonUtil;
import com.mihao.ancient_empire.common.util.RedisHelper;
import com.mihao.ancient_empire.dto.Position;
import com.mihao.ancient_empire.dto.Region;
import com.mihao.ancient_empire.dto.ws_dto.ReqUnitIndexDto;
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
    SimpUserRegistry simpUserRegistry;
    @Autowired
    RedisHelper redisHelper;
    @Autowired
    WebSocketService webSocketService;

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    @RequestMapping("hello")
    @ResponseBody
    public String getRegion(@RequestParam String code) {
        Region region = new Region();
        region.setName("山头");
        region.setBuffer(15);
        if ("1".equals(code)) {
            simpMessagingTemplate.convertAndSend("/topic/nf",region);
        }else {
            simpMessagingTemplate.convertAndSend("/queue/chat",region);
        }
        return "推送成功";
    }

    /**
     * 获取移动区域
     * @param principal
     * @param msg
     */
    @MessageMapping("/getMoveArea")
    public void getMoveArea(Principal principal, String msg) {
        log.info("从 {} getMoveArea收到的信息{}",principal.getName(), msg);
        ReqUnitIndexDto unitIndexDto = JacksonUtil.jsonToBean(msg, ReqUnitIndexDto.class);
        if (unitIndexDto != null) {
            List<Position> areas = webSocketService.getMoveArea(principal.getName(), unitIndexDto);
            simpMessagingTemplate.convertAndSendToUser(principal.getName(), "/queue/user", areas);
        }else {
            log.error("{} 解析错误", msg);
        }
//        simpMessagingTemplate.convertAndSend("/topic/room/" + principal.getName(), "replayMesToRoom");
    }


    @MessageMapping("/marco")
    public void handleChat(Principal principal, String msg) {
        Region region = new Region();
        region.setName(principal.getName());
        Set set = simpUserRegistry.getUsers();
        region.setBuffer(0);
        // 发送系统消息
        simpMessagingTemplate.convertAndSend("/topic/marco", region);
        // 单独发送消息
        simpMessagingTemplate.convertAndSendToUser(principal.getName(), "/queue/marco", region);
    }
}
