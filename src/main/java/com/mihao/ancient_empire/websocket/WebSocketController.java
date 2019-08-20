package com.mihao.ancient_empire.websocket;

import com.mihao.ancient_empire.dto.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.Set;

@Controller
public class WebSocketController {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    SimpUserRegistry simpUserRegistry;

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
