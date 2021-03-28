package pers.mihao.ancient_empire.base.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.mihao.ancient_empire.auth.service.UserService;
import pers.mihao.ancient_empire.base.constant.BaseConstant;
import pers.mihao.ancient_empire.base.dao.GameRoomDAO;
import pers.mihao.ancient_empire.base.dto.*;
import pers.mihao.ancient_empire.base.entity.GameRoom;
import pers.mihao.ancient_empire.base.entity.UserJoinRoom;
import pers.mihao.ancient_empire.base.enums.ArmyEnum;
import pers.mihao.ancient_empire.base.event.AppRoomEvent;
import pers.mihao.ancient_empire.base.service.GameRoomService;
import pers.mihao.ancient_empire.base.service.UserJoinRoomService;
import pers.mihao.ancient_empire.base.util.GameRoomIdUtil;
import pers.mihao.ancient_empire.base.util.IPageHelper;
import pers.mihao.ancient_empire.common.dto.ApiConditionDTO;
import pers.mihao.ancient_empire.common.jdbc.redis.RedisUtil;
import pers.mihao.ancient_empire.common.util.CollectionUtil;
import pers.mihao.ancient_empire.common.util.DateUtil;
import pers.mihao.ancient_empire.common.vo.AncientEmpireException;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;

/**
 * <p>
 * 服务实现类
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

    @Autowired
    UserService userService;

    @Override
    public IPage<GameRoom> getCanJoinGameRoomWithPage(ApiConditionDTO conditionDTO) {
        List<GameRoom> gameRooms = gameRoomDao.getCanJoinGameRoomWithPage(conditionDTO);
        gameRooms.forEach(gameRoom -> {
            gameRoom.setCreatTimeShow(DateUtil.formatDataTime(gameRoom.getCreateTime()));
            gameRoom.setReady(gameRoom.getJoinCount() + "/" + gameRoom.getPlayerCount());
        });
        return IPageHelper.toPage(gameRooms, conditionDTO);
    }

    @Override
    public GameRoom addRoomAndJoinRoomOwner(ReqAddRoomDTO reqAddRoomDTO) {
        GameRoom gameRoom = new GameRoom();
        gameRoom.setCreater(reqAddRoomDTO.getUserId());
        gameRoom.setCreateTime(LocalDateTime.now());
        gameRoom.setRoomOwner(reqAddRoomDTO.getUserId());
        gameRoom.setRoomId(GameRoomIdUtil.borrowRoomId());
        gameRoom.setRoomName(reqAddRoomDTO.getRoomName());
        gameRoom.setMapId(reqAddRoomDTO.getInitMap().getMapId());
        gameRoom.setMapConfig(JSON.toJSONString(reqAddRoomDTO.getInitMap()));
        gameRoom.setJoinCount(0);
        gameRoom.setPlayerCount(reqAddRoomDTO.getPlayerCount());
        // 只缓存 不入库 缓存十分钟
        RedisUtil.setJsonString(BaseConstant.AE_ROOM + gameRoom.getRoomId(), gameRoom, (long) (60 * 60 * 10));
        return gameRoom;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String playerJoinRoom(ReqRoomIdDTO reqRoomIdDTO) {
        log.info("玩家：{} 加入房间：{}", reqRoomIdDTO.getUserId(), reqRoomIdDTO.getRoomId());
        gameRoomDao.lockRoomById(reqRoomIdDTO.getRoomId());
        UserJoinRoom userJoinRoom = userJoinRoomService.getById(reqRoomIdDTO.getUserId());
        if (userJoinRoom != null) {
            log.info("玩家：{} 加入其他的房间现在退出", reqRoomIdDTO.getUserId());
            userJoinRoomService.removeById(reqRoomIdDTO.getUserId());
            applicationContext.publishEvent(new AppRoomEvent(AppRoomEvent.PLAYER_LEVEL, reqRoomIdDTO.getRoomId(), reqRoomIdDTO.getUserId()));
        }

        GameRoom gameRoom = getById(reqRoomIdDTO.getRoomId());
        gameRoom.setJoinCount(gameRoom.getJoinCount() + 1);
        InitMapDTO initMapDTO = JSON.parseObject(gameRoom.getMapConfig(), InitMapDTO.class);
        gameRoomDao.updateById(gameRoom);

        // 获取已经加入的
        List<String> joinArmyList = userJoinRoomService.getUserByRoomId(gameRoom.getRoomId())
                .stream().map(UserJoinRoom::getJoinArmy).collect(Collectors.toList());

        // 验证房间是否存在并且可以加入
        userJoinRoom = new UserJoinRoom();
        userJoinRoom.setRoomId(reqRoomIdDTO.getRoomId());
        userJoinRoom.setUserId(reqRoomIdDTO.getUserId());
        for (ArmyConfig armyConfig : initMapDTO.getArmyList()) {
            if (ArmyEnum.USER.type().equals(armyConfig.getType()) && !joinArmyList.contains(armyConfig.getColor())) {
                userJoinRoom.setJoinArmy(armyConfig.getColor());
                break;
            }
        }
        userJoinRoom.setJoinTime(LocalDateTime.now());
        userJoinRoomService.save(userJoinRoom);
        return userJoinRoom.getJoinArmy();
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void userLevelRoom(Integer id) {
        UserJoinRoom userJoinRoom = userJoinRoomService.getById(id);

        if (userJoinRoom != null) {
            gameRoomDao.lockRoomById(userJoinRoom.getRoomId());
            userJoinRoomService.removeById(id);
            GameRoom gameRoom = getById(userJoinRoom.getRoomId());
            gameRoom.setJoinCount(gameRoom.getJoinCount() - 1);
            if (gameRoom.getRoomOwner().equals(id)) {
                log.info("更换房主");
                List<Integer> userIdList = userJoinRoomService.getUserByRoomId(gameRoom.getRoomId())
                        .stream().map(UserJoinRoom::getUserId).collect(Collectors.toList());
                if (CollectionUtil.isEmpty(userIdList)) {
                    log.info("房间解散");
                    gameRoomDao.deleteById(gameRoom.getRoomId());
                } else {
                    gameRoom.setRoomOwner(userIdList.get(0));
                    gameRoomDao.updateById(gameRoom);
                    log.info("新的房主：{}", gameRoom.getRoomOwner());
                    String message = "房主离开, 新的房主：" + userService.getById(gameRoom.getRoomOwner()).getName();
                    AppRoomEvent appRoomEvent = new AppRoomEvent(AppRoomEvent.PUBLIC_MESSAGE, gameRoom.getRoomId(), message);
                    appRoomEvent.setLevelArmy(userJoinRoom.getJoinArmy());
                    applicationContext.publishEvent(appRoomEvent);
                }
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = READ_COMMITTED)
    public String changeCtlArmy(RoomArmyChangeDTO armyChangeDTO) {
        UserJoinRoom userJoinRoom = userJoinRoomService.getById(armyChangeDTO.getUserId());
        assert userJoinRoom != null;
        String oldArmy = userJoinRoom.getJoinArmy();
        gameRoomDao.lockRoomById(userJoinRoom.getRoomId());
        List<String> hasArmy = userJoinRoomService.getUserByRoomId(userJoinRoom.getRoomId())
                .stream().map(UserJoinRoom::getJoinArmy).collect(Collectors.toList());
        if (hasArmy.contains(armyChangeDTO.getNewArmy())) {
            throw new AncientEmpireException("加入重复");
        }
        userJoinRoom.setJoinArmy(armyChangeDTO.getNewArmy());
        userJoinRoomService.saveOrUpdate(userJoinRoom);
        return oldArmy;
    }

    @Override
    public List<ArmyConfig> getCurrentArmyConfigByRoomId(String id) {
        List<UserJoinRoom> userJoinRooms = userJoinRoomService.getUserByRoomId(id);
        InitMapDTO initMapDTO = JSON.parseObject(gameRoomDao.selectById(id).getMapConfig(), InitMapDTO.class);
        List<ArmyConfig> list = initMapDTO.getArmyList();

        for (ArmyConfig armyConfig : list) {
            for (UserJoinRoom userJoinRoom : userJoinRooms) {
                if (userJoinRoom.getJoinArmy().equals(armyConfig.getColor())) {
                    armyConfig.setPlayerName(userService.getUserById(userJoinRoom.getUserId()).getName());
                    armyConfig.setPlayer(userJoinRoom.getUserId().toString());
                    break;
                }
            }
        }
        return list;
    }
}
