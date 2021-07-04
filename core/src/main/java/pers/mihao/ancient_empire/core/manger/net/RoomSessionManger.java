package pers.mihao.ancient_empire.core.manger.net;

import java.util.List;
import javax.websocket.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import pers.mihao.ancient_empire.auth.entity.User;
import pers.mihao.ancient_empire.base.constant.BaseConstant;
import pers.mihao.ancient_empire.base.dto.ArmyConfig;
import pers.mihao.ancient_empire.base.dto.ReqRoomIdDTO;
import pers.mihao.ancient_empire.base.entity.GameRoom;
import pers.mihao.ancient_empire.base.event.AppRoomEvent;
import pers.mihao.ancient_empire.base.service.GameRoomService;
import pers.mihao.ancient_empire.common.annotation.Manger;
import pers.mihao.ancient_empire.common.jdbc.redis.RedisUtil;
import pers.mihao.ancient_empire.common.util.DateUtil;
import pers.mihao.ancient_empire.common.util.StringUtil;
import pers.mihao.ancient_empire.core.eums.RoomCommendEnum;
import pers.mihao.ancient_empire.core.eums.RoomEventEnum;
import pers.mihao.ancient_empire.core.eums.SendTypeEnum;
import pers.mihao.ancient_empire.core.manger.command.RoomCommand;
import pers.mihao.ancient_empire.core.manger.event.RoomEvent;
import pers.mihao.ancient_empire.core.manger.net.session.RoomSession;

/**
 * 房间session管理
 * @Author mihao
 * @Date 2021/6/16 9:39
 */
@Manger
public class RoomSessionManger extends AbstractSessionManger<RoomSession, RoomEvent> {

    Logger log = LoggerFactory.getLogger(RoomSessionManger.class);

    @Autowired
    private GameRoomService gameRoomService;


    /**
     * 监听应用类消息处理
     * @param appRoomEvent
     */
    @EventListener
    public void onGetRoomEvent(AppRoomEvent appRoomEvent) {
        log.info("收到了APP房间类型的事件" + appRoomEvent.toString());
        User player = userService.getUserById(appRoomEvent.getPlayer());
        RoomCommand roomCommand = new RoomCommand();
        // 发送组
        roomCommand.setSendType(SendTypeEnum.SEND_TO_GROUP);
        roomCommand.setUserId(String.valueOf(appRoomEvent.getPlayer()));
        roomCommand.setJoinArmy(appRoomEvent.getJoinArmy());
        roomCommand.setLevelArmy(appRoomEvent.getLevelArmy());
        switch (appRoomEvent.getEventType()) {
            case AppRoomEvent.CHANG_CTL:
                roomCommand.setRoomCommend(RoomCommendEnum.ARMY_CHANGE);
                break;
            case AppRoomEvent.PUBLIC_MESSAGE:
                roomCommand.setRoomCommend(RoomCommendEnum.SEND_MESSAGE);
                roomCommand.setMessage("【" + player.getName() + "】: " + roomCommand.getMessage());
                break;
            case AppRoomEvent.CHANG_ROOM_OWNER:
                roomCommand.setRoomCommend(RoomCommendEnum.CHANG_ROOM_OWNER);
                roomCommand.setUserId(appRoomEvent.getPlayer().toString());
            default:
                break;
        }
        if (StringUtil.isNotBlack(appRoomEvent.getSysMessage())) {
            roomCommand.setMessage("【系统消息】" + appRoomEvent.getSysMessage());
        } else if (StringUtil.isNotBlack(appRoomEvent.getMessage())) {
            roomCommand.setMessage(appRoomEvent.getMessage());
        }
        if (StringUtil.isNotBlack(roomCommand.getMessage())) {
            roomCommand.setMessage(roomCommand.getMessage() + "  " + DateUtil.getDataTime());
        }
        roomCommand.setUserName(player.getName());
        roomCommand.setUserId(player.getId().toString());
        sendMessage(roomCommand, appRoomEvent.getRoomId());
    }

    @Override
    RoomSession createSession(Session session, String typeId, User user) {
        GameRoom gameRoom = gameRoomService.getById(typeId);

        String joinGameCtlArmyColor;
        if (gameRoom == null) {
            // 首次加入
            gameRoom = RedisUtil.getObjectFromJson(BaseConstant.AE_ROOM + typeId, GameRoom.class);
            if (gameRoom != null) {
                log.info("首次加入房间 获取房间信息缓存成功{}", gameRoom);
                gameRoomService.save(gameRoom);
                ReqRoomIdDTO reqRoomIdDTO = new ReqRoomIdDTO();
                reqRoomIdDTO.setRoomId(gameRoom.getRoomId());
                reqRoomIdDTO.setUserId(user.getId());
                try {
                    joinGameCtlArmyColor = gameRoomService.playerJoinRoom(reqRoomIdDTO);
                } catch (Exception e) {
                    log.error("玩家加入房间失败", e);
                    return null;
                }
                // 处理创建一定能成功 不能就是有问题
                log.info("玩家加入房间成功");
                RedisUtil.delKey(BaseConstant.AE_ROOM + typeId);
                RoomSession roomSession = new RoomSession(typeId, user, session);
                roomSession.setJoinGameCtlArmyColor(joinGameCtlArmyColor);
                roomSession.setFirstJoinRoom(true);
                return roomSession;
            }
            return null;
        } else {
            ReqRoomIdDTO reqRoomIdDTO = new ReqRoomIdDTO();
            reqRoomIdDTO.setRoomId(typeId);
            reqRoomIdDTO.setUserId(user.getId());
            try {
                joinGameCtlArmyColor = gameRoomService.playerJoinRoom(reqRoomIdDTO);
            } catch (Exception e) {
                log.error("玩家加入房间失败", e);
                return null;
            }
            RoomSession roomSession = new RoomSession(typeId, user, session);
            roomSession.setJoinGameCtlArmyColor(joinGameCtlArmyColor);
            roomSession.setFirstJoinRoom(false);
            return roomSession;
        }

    }

    @Override
    void addNewSession(RoomSession session, List<RoomSession> sessionList) {
        RoomEvent roomEvent;
        if (StringUtil.isNotBlack(session.getJoinGameCtlArmyColor())) {
            roomEvent = new RoomEvent(session.getRoomId(), RoomEventEnum.JOIN_ROOM, session.getUser());
            roomEvent.setArmyColor(session.getJoinGameCtlArmyColor());
        } else {
            roomEvent = new RoomEvent(session.getRoomId(), RoomEventEnum.SEND_MESSAGE, session.getUser());
            roomEvent.setMessage("加入房间");
        }
        handleEvent(roomEvent);
    }

    @Override
    void removeGroup(String typeId) {
    }

    @Override
    void removeSessionFromGroup(RoomSession tSession, List<RoomSession> lastSession) {
        gameRoomService.userLevelRoom(tSession.getUser().getId());
    }

    @Override
    public void handleEvent(RoomEvent roomEvent) {
        RoomCommand roomCommand = new RoomCommand();
        switch (roomEvent.getEventType()) {
            case SEND_MESSAGE:
                roomCommand.setRoomCommend(RoomCommendEnum.SEND_MESSAGE);
                roomCommand.setMessage("【" + roomEvent.getUser().getName() + "】: " + roomEvent.getMessage());
                break;
            case JOIN_ROOM:
                roomCommand.setJoinArmy(roomEvent.getArmyColor());
                roomCommand.setRoomCommend(RoomCommendEnum.ARMY_CHANGE);
                roomCommand.setMessage("【" + roomEvent.getUser().getName() + "】: " + "加入房间");
                break;
            default:
                break;
        }
        roomCommand.setUserName(roomEvent.getUser().getName());
        roomCommand.setUserId(roomEvent.getUser().getId().toString());
        sendMessageToGroup(roomCommand, roomEvent.getId());
    }

    @Override
    protected Object getJoinSuccessMessage(String typeId) {
        List<ArmyConfig> list = gameRoomService.getCurrentArmyConfigByRoomId(typeId);
        return list;
    }


}
