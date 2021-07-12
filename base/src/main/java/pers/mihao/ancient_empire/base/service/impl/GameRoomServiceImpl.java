package pers.mihao.ancient_empire.base.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;
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
import pers.mihao.ancient_empire.base.util.AppUtil;
import pers.mihao.ancient_empire.base.util.GameRoomIdUtil;
import pers.mihao.ancient_empire.base.util.IPageHelper;
import pers.mihao.ancient_empire.common.dto.ApiConditionDTO;
import pers.mihao.ancient_empire.common.base_catch.CatchUtil;
import pers.mihao.ancient_empire.common.util.CollectionUtil;
import pers.mihao.ancient_empire.common.util.DateUtil;
import pers.mihao.ancient_empire.common.util.StringUtil;
import pers.mihao.ancient_empire.common.vo.AeException;

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
    public GameRoom addReadyRoom2Catch(ReqAddRoomDTO reqAddRoomDTO) {
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
        CatchUtil.setJsonString(BaseConstant.AE_ROOM + gameRoom.getRoomId(), gameRoom, (long) (60 * 60 * 10));
        return gameRoom;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String playerJoinRoom(ReqRoomIdDTO reqRoomIdDTO) {
        log.info("玩家：{} 加入房间：{}", reqRoomIdDTO.getUserId(), reqRoomIdDTO.getRoomId());
        GameRoom gameRoom = gameRoomDao.lockRoomById(reqRoomIdDTO.getRoomId());
        UserJoinRoom userJoinRoom = userJoinRoomService.getById(reqRoomIdDTO.getUserId());
        if (userJoinRoom != null) {
            log.info("玩家：{} 加入其他的房间现在退出", reqRoomIdDTO.getUserId());
            userJoinRoomService.removeById(reqRoomIdDTO.getUserId());
            applicationContext.publishEvent(new AppRoomEvent(AppRoomEvent.CHANG_CTL, reqRoomIdDTO.getRoomId(), reqRoomIdDTO.getUserId()));
        }

        gameRoom.setJoinCount(gameRoom.getJoinCount() + 1);

        InitMapDTO initMapDTO = JSON.parseObject(gameRoom.getMapConfig(), InitMapDTO.class);
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

        if (gameRoom.getRoomOwner() == null) {
            gameRoom.setRoomOwner(reqRoomIdDTO.getUserId());
            AppRoomEvent appRoomEvent = new AppRoomEvent(AppRoomEvent.CHANG_ROOM_OWNER, gameRoom.getRoomId());
            appRoomEvent.setSysMessage("新的房主 :" + userService.getById(reqRoomIdDTO.getUserId()).getName());
            appRoomEvent.setPlayer(reqRoomIdDTO.getUserId());
            applicationContext.publishEvent(appRoomEvent);
            updateById(gameRoom);
        } else {
            updateById(gameRoom);
        }

        return userJoinRoom.getJoinArmy();
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void userLevelRoom(Integer id) {
        UserJoinRoom userJoinRoom = userJoinRoomService.getById(id);

        if (userJoinRoom != null) {
            GameRoom gameRoom = gameRoomDao.lockRoomById(userJoinRoom.getRoomId());
            userJoinRoomService.removeById(id);
            gameRoom.setJoinCount(gameRoom.getJoinCount() - 1);
            if (id.equals(gameRoom.getRoomOwner())) {
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
                    String message = "房主离开,新的房主：" + userService.getById(gameRoom.getRoomOwner()).getName();
                    AppRoomEvent appRoomEvent = new AppRoomEvent(AppRoomEvent.CHANG_ROOM_OWNER, gameRoom.getRoomId());
                    appRoomEvent.setLevelArmy(userJoinRoom.getJoinArmy());
                    appRoomEvent.setPlayer(gameRoom.getRoomOwner());
                    appRoomEvent.setSysMessage(message);
                    applicationContext.publishEvent(appRoomEvent);
                }
            } else {
                if (gameRoom.getJoinCount() == 0) {
                    removeById(gameRoom.getRoomId());
                } else  {
                    gameRoomDao.updateById(gameRoom);
                }
            }
            AppRoomEvent appRoomEvent = new AppRoomEvent(AppRoomEvent.CHANG_CTL, gameRoom.getRoomId());
            appRoomEvent.setLevelArmy(userJoinRoom.getJoinArmy());
            appRoomEvent.setPlayer(id);
            appRoomEvent
                .setSysMessage(AppUtil.getSystemMessagePrefix() + "玩家" + userService.getById(id).getName() + "离开");
            applicationContext.publishEvent(appRoomEvent);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class, isolation = READ_COMMITTED)
    public String changeCtlArmy(RoomArmyChangeDTO armyChangeDTO) {
        UserJoinRoom userJoinRoom = userJoinRoomService.getById(armyChangeDTO.getUserId());
        assert userJoinRoom != null;
        String oldArmy = userJoinRoom.getJoinArmy();
        GameRoom gameRoom = gameRoomDao.lockRoomById(userJoinRoom.getRoomId());
        List<String> hasArmy = userJoinRoomService.getUserByRoomId(userJoinRoom.getRoomId())
                .stream().map(UserJoinRoom::getJoinArmy).collect(Collectors.toList());
        if (hasArmy.contains(armyChangeDTO.getNewArmy())) {
            throw new AeException("加入重复");
        }
        userJoinRoom.setJoinArmy(armyChangeDTO.getNewArmy());
        userJoinRoomService.saveOrUpdate(userJoinRoom);
        if (gameRoom.getRoomOwner() == null) {
            gameRoom.setRoomOwner(armyChangeDTO.getUserId());
            AppRoomEvent appRoomEvent = new AppRoomEvent(AppRoomEvent.CHANG_ROOM_OWNER, gameRoom.getRoomId());
            appRoomEvent.setSysMessage("新的房主" + userService.getById(armyChangeDTO.getUserId()).getName());
            appRoomEvent.setPlayer(armyChangeDTO.getUserId());
            applicationContext.publishEvent(appRoomEvent);
            updateById(gameRoom);
        }
        return oldArmy;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<ArmyConfig> getCurrentArmyConfigByRoomId(String id) {
        List<UserJoinRoom> userJoinRooms = userJoinRoomService.getUserByRoomId(id);
        InitMapDTO initMapDTO = JSON.parseObject(gameRoomDao.selectById(id).getMapConfig(), InitMapDTO.class);
        List<ArmyConfig> list = initMapDTO.getArmyList();

        for (ArmyConfig armyConfig : list) {
            for (UserJoinRoom userJoinRoom : userJoinRooms) {
                if (armyConfig.getColor().equals(userJoinRoom.getJoinArmy())) {
                    armyConfig.setPlayerName(userService.getUserById(userJoinRoom.getUserId()).getName());
                    armyConfig.setPlayer(userJoinRoom.getUserId().toString());
                    break;
                }
            }
        }
        return list;
    }

    @Override
    public void levelCtlArmy(RoomArmyLevelDTO levelDTO) {
        gameRoomDao.lockRoomById(levelDTO.getRoomId());
        UserJoinRoom userJoinRoom = userJoinRoomService.getById(levelDTO.getPlayerId());
        userJoinRoom.setJoinArmy(null);
        userJoinRoomService.updateById(userJoinRoom);

        GameRoom gameRoom = getById(levelDTO.getRoomId());

        if (levelDTO.getPlayerId().equals(gameRoom.getRoomOwner())) {
            log.info("改变房主");
            List<UserJoinRoom> userJoinRooms = userJoinRoomService.getUserByRoomId(gameRoom.getRoomId());
            List<UserJoinRoom> ctlArmyList = userJoinRooms.stream()
                    .filter(u -> StringUtil.isNotBlack(u.getJoinArmy()))
                    .sorted(Comparator.comparingLong(u -> u.getJoinTime().toEpochSecond(ZoneOffset.UTC)))
                    .collect(Collectors.toList());
            if (ctlArmyList.size() > 0) {
                UserJoinRoom newRoomOwner = ctlArmyList.get(0);
                gameRoom.setRoomOwner(newRoomOwner.getUserId());
                AppRoomEvent appRoomEvent = new AppRoomEvent(AppRoomEvent.CHANG_ROOM_OWNER, gameRoom.getRoomId());
                appRoomEvent.setSysMessage("房主退出,新的房主" + userService.getById(newRoomOwner.getUserId()).getName());
                appRoomEvent.setPlayer(newRoomOwner.getUserId());
                applicationContext.publishEvent(appRoomEvent);
            } else {
                gameRoom.setRoomOwner(null);
            }
        }
        updateById(gameRoom);


    }
}
