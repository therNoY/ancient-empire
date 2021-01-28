package pers.mihao.ancient_empire.core.controller;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pers.mihao.ancient_empire.auth.util.AuthUtil;
import pers.mihao.ancient_empire.base.bo.Army;
import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.bo.UnitInfo;
import pers.mihao.ancient_empire.base.dto.ReqInitMapDto;
import pers.mihao.ancient_empire.base.entity.UnitMes;
import pers.mihao.ancient_empire.base.entity.UserMap;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.base.enums.AbilityEnum;
import pers.mihao.ancient_empire.base.enums.GameTypeEnum;
import pers.mihao.ancient_empire.base.service.UnitMesService;
import pers.mihao.ancient_empire.base.service.UserMapService;
import pers.mihao.ancient_empire.base.service.UserRecordService;
import pers.mihao.ancient_empire.base.util.AppUtil;
import pers.mihao.ancient_empire.base.vo.GameVO;
import pers.mihao.ancient_empire.common.util.EnumUtil;
import pers.mihao.ancient_empire.common.util.RespUtil;
import pers.mihao.ancient_empire.common.vo.RespJson;
import pers.mihao.ancient_empire.core.constans.ExtMes;
import pers.mihao.ancient_empire.core.dto.SendMessageDTO;
import pers.mihao.ancient_empire.core.eums.GameCommendEnum;
import pers.mihao.ancient_empire.core.eums.SendTypeEnum;
import pers.mihao.ancient_empire.core.manger.GameContext;
import pers.mihao.ancient_empire.core.manger.GameCoreManger;
import pers.mihao.ancient_empire.core.manger.GameSessionManger;
import pers.mihao.ancient_empire.core.manger.command.GameCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @auther mihao
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

    /**
     * 用于单机遭遇战，联机遭遇战
     * 通过用户设置的地图初始环境设置初始
     *
     * @param reqInitMapDto
     * @param result
     * @return
     */
    @PostMapping("/api/record/init")
    public RespJson initMapRecord(@RequestBody @Validated ReqInitMapDto reqInitMapDto, BindingResult result) {
        // 1.获取用户地图
        UserMap userMap = userMapService.getEncounterMapById(reqInitMapDto.getMapId());
        if (userMap == null) {
            log.error("错误的地图信息{}", reqInitMapDto);
            return RespUtil.error();
        }

        // TODO 检测是否达到最大游戏数量

        // 2.生成文档注册上下文
        UserRecord userRecord = userRecordService.initMapRecord(reqInitMapDto, userMap);
        log.info("生成新的存档：{}， 注册游戏上下文", userRecord.getUuid());
        gameCoreManger.registerGameContext(userRecord, EnumUtil.valueOf(GameTypeEnum.class, reqInitMapDto.getGameType()), 1);

        // 3.返回前端保存
        GameVO userMapVo = new GameVO();
        BeanUtils.copyProperties(userRecord, userMapVo);
        return RespUtil.successResJson(userMapVo);
    }

    /**
     * 获取可购买的所有的单位的详细信息
     */
    @GetMapping("/unitInfo/list")
    public RespJson getUnitInfoList(@RequestParam String uuid) {
        GameContext context = gameCoreManger.getGameSessionById(uuid);
        UserRecord record = context.getUserRecord();
        if (record == null) {
            return RespUtil.error(40010);
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
        return RespUtil.successResJson(respUnitMes);
    }


    /**
     * 发送消息
     * @param sendMessageDTO
     * @return
     */
    @PostMapping("/api/message/send")
    public RespJson sendMessage(@RequestBody SendMessageDTO sendMessageDTO) {
        switch (sendMessageDTO.getSendTypeEnum()) {
            case SEND_TO_GAME:
                String gameId = gameSessionManger.getUserGameId(AuthUtil.getUserId());
                GameCommand command = new GameCommand();
                command.setGameCommendEnum(GameCommendEnum.SHOW_GAME_NEWS);
                JSONObject extData = new JSONObject(2);
                extData.put(ExtMes.MESSAGE, sendMessageDTO.getMessage());
                command.setExtMes(extData);
                command.setSendTypeEnum(SendTypeEnum.SEND_TO_GAME);
                gameSessionManger.sendMessage(command, gameId);
                break;
            case SEND_TO_SYSTEM:
            case SEND_TO_USER:
                break;
        }
        return RespUtil.successResJson();
    }

}
