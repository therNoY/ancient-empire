package pers.mihao.ancient_empire.base.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.mihao.ancient_empire.base.dto.ArmyConfig;
import pers.mihao.ancient_empire.base.dto.ReqAddRoomDTO;
import pers.mihao.ancient_empire.base.dto.ReqRoomIdDTO;
import pers.mihao.ancient_empire.base.dto.RoomArmyChangeDTO;
import pers.mihao.ancient_empire.base.dto.RoomArmyLevelDTO;
import pers.mihao.ancient_empire.base.entity.GameRoom;
import pers.mihao.ancient_empire.base.enums.ArmyEnum;
import pers.mihao.ancient_empire.base.event.AppRoomEvent;
import pers.mihao.ancient_empire.base.service.GameRoomService;
import pers.mihao.ancient_empire.base.service.UserJoinRoomService;
import pers.mihao.ancient_empire.common.dto.ApiConditionDTO;
import pers.mihao.ancient_empire.common.util.StringUtil;
import pers.mihao.ancient_empire.common.vo.AeException;

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
    public IPage<GameRoom> listRoomWithPage(@RequestBody ApiConditionDTO conditionDTO) {
        return gameRoomService.getCanJoinGameRoomWithPage(conditionDTO);
    }

    /**
     * 创建房间
     *
     * @param reqAddRoomDTO
     * @return
     */
    @RequestMapping("/api/room/save")
    public GameRoom saveRoom(@RequestBody ReqAddRoomDTO reqAddRoomDTO) {
        int playCount = 0;
        for (ArmyConfig armyConfig : reqAddRoomDTO.getInitMap().getArmyList()) {
            if (armyConfig.getType().equals(ArmyEnum.USER.type())) {
                playCount ++;
            }
        }
        if (playCount == 0) {
            throw new AeException("玩家不能为空");
        }
        reqAddRoomDTO.setPlayerCount(playCount);
        return gameRoomService.addRoomAndJoinRoomOwner(reqAddRoomDTO);
    }

    /**
     * 玩家加入
     *
     * @param reqRoomIdDTO
     * @return
     */
    @RequestMapping("/api/room/playerJoin")
    public void playerJoinRoom(@RequestBody ReqRoomIdDTO reqRoomIdDTO) {
        String color = gameRoomService.playerJoinRoom(reqRoomIdDTO);
        if (StringUtil.isBlack(color)) {
            throw new AeException();
        }
        AppRoomEvent appRoomEvent = new AppRoomEvent(AppRoomEvent.CHANG_CTL, reqRoomIdDTO.getRoomId(), reqRoomIdDTO.getUserId());
        appRoomEvent.setJoinArmy(color);
        applicationContext.publishEvent(appRoomEvent);
    }

    /**
     * 玩家离开
     *
     * @param reqRoomIdDTO
     * @return
     */
    @RequestMapping("/api/room/playerLevel")
    public void playerLevelRoom(@RequestBody ReqRoomIdDTO reqRoomIdDTO) {
//        boolean res = gameRoomService.playerLevelRoom(reqRoomIdDTO);
    }


    /**
     * 玩家改变军队
     *
     * @param reqRoomIdDTO
     * @return
     */
    @RequestMapping("/api/room/changeArmy")
    public void changeCtlArmy(@RequestBody RoomArmyChangeDTO roomArmyChangeDTO) {
        String levelArmy = null;
        try {
            levelArmy = gameRoomService.changeCtlArmy(roomArmyChangeDTO);
            String roomId = userJoinRoomService.getById(roomArmyChangeDTO.getUserId()).getRoomId();
            AppRoomEvent appRoomEvent = new AppRoomEvent(AppRoomEvent.CHANG_CTL, roomId);
            appRoomEvent.setLevelArmy(levelArmy);
            appRoomEvent.setJoinArmy(roomArmyChangeDTO.getNewArmy());
            appRoomEvent.setPlayer(roomArmyChangeDTO.getUserId());
            applicationContext.publishEvent(appRoomEvent);
        } catch (Exception e) {
            throw new AeException(e);
        }
    }


    /**
     * 玩家改变军队
     *
     * @param reqRoomIdDTO
     * @return
     */
    @RequestMapping("/api/room/levelCtlArmy")
    public void levelCtlArmy(@RequestBody RoomArmyLevelDTO levelDTO) {
        try {
            String roomId = userJoinRoomService.getById(levelDTO.getUserId()).getRoomId();
            levelDTO.setRoomId(roomId);
            gameRoomService.levelCtlArmy(levelDTO);
            AppRoomEvent appRoomEvent = new AppRoomEvent(AppRoomEvent.CHANG_CTL, roomId);
            appRoomEvent.setLevelArmy(levelDTO.getColor());
            appRoomEvent.setPlayer(levelDTO.getPlayerId());
            applicationContext.publishEvent(appRoomEvent);
        } catch (Exception e) {
            throw new AeException(e);
        }
    }

}
