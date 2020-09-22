package pers.mihao.ancient_empire.core.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pers.mihao.ancient_empire.base.dto.ReqInitMapDto;
import pers.mihao.ancient_empire.base.entity.UserMap;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.base.enums.GameTypeEnum;
import pers.mihao.ancient_empire.base.service.UserMapService;
import pers.mihao.ancient_empire.base.service.UserRecordService;
import pers.mihao.ancient_empire.base.vo.GameVO;
import pers.mihao.ancient_empire.base.vo.UserMapVo;
import pers.mihao.ancient_empire.common.util.EnumUtil;
import pers.mihao.ancient_empire.common.util.RespUtil;
import pers.mihao.ancient_empire.common.vo.RespJson;
import pers.mihao.ancient_empire.core.manger.GameCoreManger;

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
    GameCoreManger gameCoreManger;

    /**
     * 用于单机遭遇战，联机遭遇战
     * 通过用户设置的地图初始环境设置初始
     *
     * @param reqInitMapDto
     * @param result
     * @return
     */
    @PostMapping("/record/init")
    public RespJson initMapRecord(@RequestBody @Validated ReqInitMapDto reqInitMapDto, BindingResult result) {
        // 1.获取用户地图
        UserMap userMap = userMapService.getEncounterMapById(reqInitMapDto.getMapId());
        if (userMap == null) {
            log.error("错误的地图信息{}", reqInitMapDto);
            return RespUtil.error();
        }
        // 2.生成文档注册上下文
        UserRecord userRecord = userRecordService.initMapRecord(reqInitMapDto, userMap);
        log.info("生成新的存档：{}， 注册游戏上下文", userRecord.getUuid());
        gameCoreManger.registerGameContext(userRecord, EnumUtil.valueOf(GameTypeEnum.class, reqInitMapDto.getGameType()));

        // 2.返回前端保存
        GameVO userMapVo = new GameVO();
        BeanUtils.copyProperties(userRecord, userMapVo);
        userMapVo.setCastleTitles(userRecordService.findCastleTitle(userMap.getRegions(), userMap.getRow(), userMap.getColumn()));
        return RespUtil.successResJson(userMapVo);
    }
}
