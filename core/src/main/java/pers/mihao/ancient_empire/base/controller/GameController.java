package pers.mihao.ancient_empire.base.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pers.mihao.ancient_empire.auth.util.LoginUserHolder;
import pers.mihao.ancient_empire.base.bo.Army;
import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.bo.UnitInfo;
import pers.mihao.ancient_empire.base.constant.BaseConstant;
import pers.mihao.ancient_empire.base.dto.ArmyConfig;
import pers.mihao.ancient_empire.base.dto.InitMapDTO;
import pers.mihao.ancient_empire.base.dto.ReqRecordContinueDTO;
import pers.mihao.ancient_empire.base.dto.ReqRoomIdDTO;
import pers.mihao.ancient_empire.base.entity.*;
import pers.mihao.ancient_empire.base.enums.AbilityEnum;
import pers.mihao.ancient_empire.base.enums.ArmyEnum;
import pers.mihao.ancient_empire.base.enums.ColorEnum;
import pers.mihao.ancient_empire.base.enums.GameTypeEnum;
import pers.mihao.ancient_empire.base.service.*;
import pers.mihao.ancient_empire.base.util.AppUtil;
import pers.mihao.ancient_empire.common.util.EnumUtil;
import pers.mihao.ancient_empire.common.util.StringUtil;
import pers.mihao.ancient_empire.common.vo.AeException;
import pers.mihao.ancient_empire.core.constans.ExtMes;
import pers.mihao.ancient_empire.core.dto.SendMessageDTO;
import pers.mihao.ancient_empire.core.eums.GameCommendEnum;
import pers.mihao.ancient_empire.core.eums.RoomCommendEnum;
import pers.mihao.ancient_empire.core.eums.SendTypeEnum;
import pers.mihao.ancient_empire.core.manger.GameContext;
import pers.mihao.ancient_empire.core.manger.GameCoreManger;
import pers.mihao.ancient_empire.core.manger.command.RoomCommand;
import pers.mihao.ancient_empire.core.manger.net.GameSessionManger;
import pers.mihao.ancient_empire.core.manger.command.GameCommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author mihao
 * @version 1.0
 * @date 2020\9\20 0020 22:10
 */
@RestController
public class GameController {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserRecordService userRecordService;
    @Autowired
    UserMapService userMapService;
    @Autowired
    UnitMesService unitMesService;
    @Autowired
    GameCoreManger gameCoreManger;
    @Autowired
    GameSessionManger gameSessionManger;
    @Autowired
    GameRoomService roomService;
    @Autowired
    UserJoinRoomService userJoinRoomService;

    /**
     * 用于单机遭遇战
     *
     * @param initMapDTO
     * @param result
     * @return
     */
    @PostMapping("/api/map/init")
    public UserRecord registerUserMap(@RequestBody @Validated InitMapDTO initMapDTO, BindingResult result) {
        int playerCount = 1;
        initMapDTO.setPlayer(new HashMap<>(16));
        // 1.获取用户地图
        UserMap userMap = userMapService.getUserMapById(initMapDTO.getMapId());
        if (userMap == null) {
            log.error("错误的地图信息{}", initMapDTO);
            throw new AeException();
        }
        if (GameTypeEnum.STORY.type().equals(initMapDTO.getGameType())) {
            List<String> colorList = userMap.getUnits().stream().map(u -> u.getColor()).collect(Collectors.toList());
            initMapDTO.setArmyList(getDefaultArmyConfig().stream()
                .filter(armyConfig -> colorList.contains(armyConfig.getColor()))
                .collect(Collectors.toList()));

            if (initMapDTO.getArmyList().size() == 1) {
                // 这里强直加上一个新的阵营
                ArmyConfig army = new ArmyConfig();
                army.setColor(ColorEnum.RED.type());
                army.setCamp(2);
                army.setType(ArmyEnum.AI.type());
                army.setOrder(2);
                army.setUnits(new ArrayList<>());
                initMapDTO.getArmyList().add(army);
            }
        }
        // 2.生成文档注册上下文
        UserRecord userRecord = userRecordService.initMapRecord(initMapDTO, userMap);
        log.info("生成新的存档：{}， 注册游戏上下文", userRecord.getUuid());
        gameCoreManger.registerGameContext(userRecord, EnumUtil.valueOf(GameTypeEnum.class, initMapDTO.getGameType()),
            playerCount);

        // 3.返回前端保存
        return userRecord;
    }

    private List<ArmyConfig> getDefaultArmyConfig() {
        List<ArmyConfig> armyList = new ArrayList<>();
        ArmyConfig armyConfig = new ArmyConfig();
        armyConfig.setType(ArmyEnum.USER.type());
        armyConfig.setColor(ColorEnum.BLUE.type());
        armyConfig.setCamp(1);
        armyConfig.setOrder(1);
        armyList.add(armyConfig);

        ArmyConfig armyConfig2 = new ArmyConfig();
        armyConfig2.setType(ArmyEnum.AI.type());
        armyConfig2.setColor(ColorEnum.RED.type());
        armyConfig2.setCamp(2);
        armyConfig2.setOrder(2);
        armyList.add(armyConfig2);

        ArmyConfig armyConfig3 = new ArmyConfig();
        armyConfig3.setType(ArmyEnum.USER.type());
        armyConfig3.setColor(ColorEnum.GREEN.type());
        armyConfig3.setCamp(1);
        armyConfig3.setOrder(3);
        armyList.add(armyConfig3);

        ArmyConfig armyConfig4 = new ArmyConfig();
        armyConfig4.setType(ArmyEnum.AI.type());
        armyConfig4.setColor(ColorEnum.BLACK.type());
        armyConfig4.setCamp(2);
        armyConfig4.setOrder(4);
        armyList.add(armyConfig4);
        return armyList;
    }

    /**
     * 用于单机遭遇战，联机遭遇战 通过用户设置的地图初始环境设置初始
     *
     * @param initMapDTO
     * @param result
     * @return
     */
    @PostMapping("/api/record/continue")
    public UserRecord registerUserMap(@RequestBody ReqRecordContinueDTO continueDTO) {
        UserRecord record = userRecordService.getRecordById(continueDTO.getUuid());
        UserRecord userRecord = userRecordService.getRecordById(continueDTO.getUuid());
        userRecord.setUuid(StringUtil.getUUID());
        userRecord.setUnSave(BaseConstant.YES);
        userRecord.setCreateTime(LocalDateTime.now());
        userRecord.setRecordName("系统保存");
        userRecordService.saveRecord(userRecord);
        userRecordService.delOtherUnSaveStandRecord(userRecord.getUuid(), continueDTO.getUserId());
        gameCoreManger.registerGameContext(record, EnumUtil.valueOf(GameTypeEnum.class, record.getType()), 1);
        // 3.返回前端保存
        return record;
    }


    /**
     * 多人游戏入口
     *
     * @param initMapDTO
     * @param result
     * @return
     */
    @PostMapping("/api/room/init")
    public UserRecord registerFormRoom(@RequestBody ReqRoomIdDTO reqRoomIdDTO) {
        GameRoom gameRoom = roomService.getById(reqRoomIdDTO.getRoomId());
        InitMapDTO initMapDTO = JSON.parseObject(gameRoom.getMapConfig(), InitMapDTO.class);
        initMapDTO.setUserId(LoginUserHolder.getUserId());
        initMapDTO.setGameType(GameTypeEnum.ROOM.type());
        List<UserJoinRoom> userJoinRooms = userJoinRoomService.getUserByRoomId(reqRoomIdDTO.getRoomId());
        int playerCount = (int) userJoinRooms.stream()
            .filter(userJoinRoom -> StringUtil.isNotBlack(userJoinRoom.getJoinArmy())).count();
        initMapDTO.setPlayer(
            userJoinRooms.stream().filter(userJoinRoom -> StringUtil.isNotBlack(userJoinRoom.getJoinArmy()))
                .collect(Collectors.toMap(UserJoinRoom::getJoinArmy, u -> u.getUserId().toString()))
        );
        // 设置没有参加的人为人机
        for (ArmyConfig armyConfig : initMapDTO.getArmyList()) {
            if (armyConfig.getType().equals(ArmyEnum.USER.type()) && !initMapDTO.getPlayer()
                .containsKey(armyConfig.getColor())) {
                armyConfig.setType(ArmyEnum.AI.type());
            }
        }

        // 1.获取用户地图
        UserMap userMap = userMapService.getEncounterMapById(initMapDTO.getMapId());
        // 2.生成文档注册上下文
        UserRecord userRecord = userRecordService.initMapRecord(initMapDTO, userMap);

        log.info("生成新的存档：{}， 注册游戏上下文", userRecord.getUuid());
        gameCoreManger.registerGameContext(userRecord, EnumUtil.valueOf(GameTypeEnum.class, initMapDTO.getGameType()),
            playerCount);
        RoomCommand roomCommend = new RoomCommand();
        roomCommend.setRoomCommend(RoomCommendEnum.START_GAME);
        roomCommend.setRecordId(userRecord.getUuid());
        roomCommend.setMessage("准备开始游戏...");
        gameSessionManger.sendMessage2Room(roomCommend, reqRoomIdDTO.getRoomId());
        // 3.返回前端保存
        return userRecord;
    }

    /**
     * 获取可购买的所有的单位的详细信息
     */
    @GetMapping("/unitInfo/list")
    public List<UnitInfo> getUnitInfoList(@RequestParam String uuid) {
        GameContext context = gameCoreManger.getGameSessionById(uuid);
        UserRecord record = context.getUserRecord();
        if (record == null) {
            throw new AeException(40010);
        }
        // 获取当前模板可以购买的单位
        List<UnitMes> unitInfoList = unitMesService.getCanBuyUnit(context.getUserTemplate().getId());
        // 排除存活的领主
        Army army = AppUtil.getCurrentArmy(record);
        UnitInfo unitInfo;
        List<Integer> aliveLoaderId = new ArrayList<>();
        for (Unit unit : army.getUnits()) {
            unitInfo = unitMesService.getUnitInfo(unit.getTypeId(), unit.getLevel());
            if (unitInfo.getAbilities().contains(AbilityEnum.CASTLE_GET.ability())) {
                aliveLoaderId.add(unit.getTypeId());
            }
        }
        List<UnitInfo> respUnitMes = unitInfoList.stream()
            .filter(unitMes -> !aliveLoaderId.contains(unitMes.getId()))
            .map(unitMes -> unitMesService.getUnitInfo(unitMes.getId(), 0))
            .collect(Collectors.toList());
        respUnitMes = context.filterUnit(respUnitMes);

        return respUnitMes;
    }


    /**
     * 发送消息
     *
     * @param sendMessageDTO
     * @return
     */
    @PostMapping("/api/message/send")
    public void sendMessage(@RequestBody SendMessageDTO sendMessageDTO) {
        switch (sendMessageDTO.getSendTypeEnum()) {
            case SEND_TO_GAME:
                String gameId = gameSessionManger.getUserGameId(LoginUserHolder.getUserId());
                GameCommand command = new GameCommand();
                command.setGameCommendEnum(GameCommendEnum.SHOW_GAME_NEWS);
                JSONObject extData = new JSONObject(2);
                extData.put(ExtMes.MESSAGE, sendMessageDTO.getMessage());
                command.setExtMes(extData);
                command.setSendTypeEnum(SendTypeEnum.SEND_TO_GAME);
                gameSessionManger.sendMessage(command, gameId);
                break;
            case SEND_TO_SYSTEM:
            case SEND_TO_GAME_USER:
            default:
                break;
        }
    }

}
