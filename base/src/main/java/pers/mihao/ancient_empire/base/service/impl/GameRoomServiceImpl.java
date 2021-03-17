package pers.mihao.ancient_empire.base.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.mihao.ancient_empire.base.dao.GameRoomDAO;
import pers.mihao.ancient_empire.base.dto.ReqAddRoomDTO;
import pers.mihao.ancient_empire.base.dto.ReqRoomIdDTO;
import pers.mihao.ancient_empire.base.entity.GameRoom;
import pers.mihao.ancient_empire.base.entity.UserJoinRoom;
import pers.mihao.ancient_empire.base.event.RoomEvent;
import pers.mihao.ancient_empire.base.service.GameRoomService;
import pers.mihao.ancient_empire.base.service.UserJoinRoomService;
import pers.mihao.ancient_empire.base.util.GameRoomIdUtil;
import pers.mihao.ancient_empire.base.util.IPageHelper;
import pers.mihao.ancient_empire.common.dto.ApiConditionDTO;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author mihao
 * @since 2021-03-02
 */
@Service
public class GameRoomServiceImpl extends ServiceImpl<GameRoomDAO, GameRoom> implements GameRoomService {

    private static final Logger log = LoggerFactory.getLogger(GameRoomServiceImpl.class);

    @Autowired
    GameRoomDAO gameRoomDao;

    @Autowired
    UserJoinRoomService userJoinRoomService;

    @Autowired
    ApplicationContext applicationContext;

    @Override
    public IPage<GameRoom> getCanJoinGameRoomWithPage(ApiConditionDTO conditionDTO) {
        List<GameRoom> gameRooms = gameRoomDao.getCanJoinGameRoomWithPage(conditionDTO);
        return IPageHelper.toPage(gameRooms, conditionDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GameRoom addRoomAndJoinRoomOwner(ReqAddRoomDTO reqAddRoomDTO) {
        GameRoom gameRoom = new GameRoom();
        gameRoom.setCreater(reqAddRoomDTO.getUserId());
        gameRoom.setCreateTime(LocalDateTime.now());
        gameRoom.setRoomOwner(reqAddRoomDTO.getUserId());
        gameRoom.setRoomId(GameRoomIdUtil.borrowRoomId());
        gameRoom.setRoomName(reqAddRoomDTO.getRoomName());
        gameRoom.setMapId(reqAddRoomDTO.getMapId());
        save(gameRoom);

        ReqRoomIdDTO reqRoomIdDTO = new ReqRoomIdDTO();
        reqRoomIdDTO.setRoomId(gameRoom.getRoomId());
        reqRoomIdDTO.setUserId(reqAddRoomDTO.getUserId());
        playerJoinRoom(reqRoomIdDTO);

        return gameRoom;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean playerJoinRoom(ReqRoomIdDTO reqRoomIdDTO) {
        log.info("玩家：{} 加入房间：{}", reqRoomIdDTO.getUserId(), reqRoomIdDTO.getRoomId());
        gameRoomDao.lockRoomById(reqRoomIdDTO.getRoomId());
        UserJoinRoom userJoinRoom = userJoinRoomService.getById(reqRoomIdDTO.getUserId());
        if (userJoinRoom != null) {
            log.info("玩家：{} 加入其他的房间现在退出", reqRoomIdDTO.getUserId());
            userJoinRoomService.removeById(reqRoomIdDTO.getUserId());
            applicationContext.publishEvent(new RoomEvent(RoomEvent.PLAYER_LEVEL, reqRoomIdDTO.getRoomId(), reqRoomIdDTO.getUserId()));
        }
        // 验证房间是否存在并且可以加入
        userJoinRoom = new UserJoinRoom();
        userJoinRoom.setRoomId(reqRoomIdDTO.getRoomId());
        userJoinRoom.setUserId(reqRoomIdDTO.getUserId());
        userJoinRoomService.save(userJoinRoom);
        applicationContext.publishEvent(new RoomEvent(RoomEvent.PLAYER_JOIN, reqRoomIdDTO.getRoomId(), reqRoomIdDTO.getUserId()));
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean playerLevelRoom(ReqRoomIdDTO reqRoomIdDTO) {
        gameRoomDao.lockRoomById(reqRoomIdDTO.getRoomId());
        UserJoinRoom userJoinRoom = userJoinRoomService.getById(reqRoomIdDTO.getUserId());
        if (userJoinRoom != null) {
            log.info("玩家：{} 退出 房间：{}", reqRoomIdDTO.getUserId(), reqRoomIdDTO.getRoomId());
            userJoinRoomService.removeById(reqRoomIdDTO.getUserId());
            applicationContext.publishEvent(new RoomEvent(RoomEvent.PLAYER_LEVEL, reqRoomIdDTO.getRoomId(), reqRoomIdDTO.getUserId()));
        }
        return true;
    }

}
