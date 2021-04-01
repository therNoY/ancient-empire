package pers.mihao.ancient_empire.base.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.mihao.ancient_empire.base.dto.*;
import pers.mihao.ancient_empire.base.entity.GameRoom;
import pers.mihao.ancient_empire.base.enums.ArmyEnum;
import pers.mihao.ancient_empire.base.event.AppRoomEvent;
import pers.mihao.ancient_empire.base.service.GameRoomService;
import pers.mihao.ancient_empire.base.service.UserJoinRoomService;
import pers.mihao.ancient_empire.common.dto.ApiConditionDTO;
import pers.mihao.ancient_empire.common.util.RespUtil;
import pers.mihao.ancient_empire.common.util.StringUtil;
import pers.mihao.ancient_empire.common.vo.RespJson;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author mihao
 * @since 2021-03-02
 */
@RestController
public class GameRoomController {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    GameRoomService gameRoomService;

    @Autowired
    UserJoinRoomService userJoinRoomService;

    @Autowired
    ApplicationContext applicationContext;

    /**
     * 查看房间列表
     *
     * @param conditionDTO
     * @return
     */
    @RequestMapping("/api/room/list")
    public RespJson listRoomWithPage(@RequestBody ApiConditionDTO conditionDTO) {
        IPage<GameRoom> page = gameRoomService.getCanJoinGameRoomWithPage(conditionDTO);
        return RespUtil.successPageResJson(page);
    }

    /**
     * 创建房间
     *
     * @param reqAddRoomDTO
     * @return
     */
    @RequestMapping("/api/room/save")
    public RespJson saveRoom(@RequestBody ReqAddRoomDTO reqAddRoomDTO) {
        int playCount = 0;
        for (ArmyConfig armyConfig : reqAddRoomDTO.getInitMap().getArmyList()) {
            if (armyConfig.getType().equals(ArmyEnum.USER.type())) {
                playCount ++;
            }
        }
        if (playCount == 0) {
            return RespUtil.error("玩家不能为空");
        }
        reqAddRoomDTO.setPlayerCount(playCount);
        GameRoom room = gameRoomService.addRoomAndJoinRoomOwner(reqAddRoomDTO);
        return RespUtil.successResJson(room);
    }

    /**
     * 玩家加入
     *
     * @param reqRoomIdDTO
     * @return
     */
    @RequestMapping("/api/room/playerJoin")
    public RespJson playerJoinRoom(@RequestBody ReqRoomIdDTO reqRoomIdDTO) {
        String color = gameRoomService.playerJoinRoom(reqRoomIdDTO);
        if (StringUtil.isNotBlack(color)) {
            AppRoomEvent appRoomEvent = new AppRoomEvent(AppRoomEvent.CHANG_CTL, reqRoomIdDTO.getRoomId(), reqRoomIdDTO.getUserId());
            appRoomEvent.setJoinArmy(color);
            applicationContext.publishEvent(appRoomEvent);
            return RespUtil.successResJson();
        }
        return RespUtil.error();
    }

    /**
     * 玩家离开
     *
     * @param reqRoomIdDTO
     * @return
     */
    @RequestMapping("/api/room/playerLevel")
    public RespJson playerLevelRoom(@RequestBody ReqRoomIdDTO reqRoomIdDTO) {
//        boolean res = gameRoomService.playerLevelRoom(reqRoomIdDTO);
        return RespUtil.successResJson();
    }


    /**
     * 玩家改变军队
     *
     * @param reqRoomIdDTO
     * @return
     */
    @RequestMapping("/api/room/changeArmy")
    public RespJson changeCtlArmy(@RequestBody RoomArmyChangeDTO roomArmyChangeDTO) {
        String levelArmy = null;
        try {
            levelArmy = gameRoomService.changeCtlArmy(roomArmyChangeDTO);
            String roomId = userJoinRoomService.getById(roomArmyChangeDTO.getUserId()).getRoomId();
            AppRoomEvent appRoomEvent = new AppRoomEvent(AppRoomEvent.CHANG_CTL, roomId);
            appRoomEvent.setLevelArmy(levelArmy);
            appRoomEvent.setJoinArmy(roomArmyChangeDTO.getNewArmy());
            appRoomEvent.setPlayer(roomArmyChangeDTO.getUserId());
            applicationContext.publishEvent(appRoomEvent);
            return RespUtil.successResJson();
        } catch (Exception e) {
            log.error("", e);
            return RespUtil.error();
        }
    }


    /**
     * 玩家改变军队
     *
     * @param reqRoomIdDTO
     * @return
     */
    @RequestMapping("/api/room/levelCtlArmy")
    public RespJson levelCtlArmy(@RequestBody RoomArmyLevelDTO levelDTO) {
        try {
            String roomId = userJoinRoomService.getById(levelDTO.getUserId()).getRoomId();
            levelDTO.setRoomId(roomId);
            gameRoomService.levelCtlArmy(levelDTO);
            AppRoomEvent appRoomEvent = new AppRoomEvent(AppRoomEvent.CHANG_CTL, roomId);
            appRoomEvent.setLevelArmy(levelDTO.getColor());
            appRoomEvent.setPlayer(levelDTO.getPlayerId());
            applicationContext.publishEvent(appRoomEvent);
            return RespUtil.successResJson();
        } catch (Exception e) {
            log.error("", e);
            return RespUtil.error();
        }
    }

}
