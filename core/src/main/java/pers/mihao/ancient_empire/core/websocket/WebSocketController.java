package pers.mihao.ancient_empire.core.websocket;

import com.alibaba.fastjson.JSON;
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
import pers.mihao.ancient_empire.core.RobotActive;
import pers.mihao.ancient_empire.core.dto.*;
import pers.mihao.ancient_empire.core.eums.WSPath;
import pers.mihao.ancient_empire.core.eums.WsMethodEnum;
import pers.mihao.ancient_empire.core.eums.ai.AiActiveEnum;
import pers.mihao.ancient_empire.core.manger.GameCoreStaManger;
import pers.mihao.ancient_empire.core.util.WsRespHelper;
import pers.mihao.ancient_empire.core.websocket.service.*;

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
//    @Autowired
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
     * 请求结束回合
     *
     * @param principal
     */
    @MessageMapping("/ws/getNewRound")
    public void getNewRound(Principal principal) {
        log.info("从 {} getSummonResult 收到的信息", principal.getName());
        RespNewRoundDto newRoundDto = newRoundService.getNewRound(principal.getName());
        GameMap map = newRoundDto.getRecord().getGameMap();
        newRoundDto.getRecord().setGameMap(null);
        simpMessagingTemplate.convertAndSendToUser(principal.getName(),
                WSPath.TOPIC_USER, WsRespHelper.success(WsMethodEnum.NEW_ROUND.type(), newRoundDto));

        // 判断新的回合是不是机器人
        if (AppUtil.getCurrentArmy(newRoundDto.getRecord()).getPlayer() == null) {
            log.info("======================接下来是机器人的行动============================");
            newRoundDto.getRecord().setGameMap(map);
            RobotActive active = new RobotActive(newRoundDto.getRecord(), AiActiveEnum.SELECT_UNIT);
            GameCoreStaManger gameCoreStaManger = GameCoreStaManger.getInstance(newRoundDto.getRecord());
            gameCoreStaManger.saveRecord(newRoundDto.getRecord());
            gameCoreStaManger.submitActive(active);
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
