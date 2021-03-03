package pers.mihao.ancient_empire.base.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import pers.mihao.ancient_empire.base.entity.GameRoom;
import pers.mihao.ancient_empire.base.service.GameRoomService;
import pers.mihao.ancient_empire.common.dto.ApiConditionDTO;
import pers.mihao.ancient_empire.common.util.RespUtil;
import pers.mihao.ancient_empire.common.vo.RespJson;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author mihao
 * @since 2021-03-02
 */
@RestController
public class GameRoomController {

    @Autowired
    GameRoomService gameRoomService;

    @RequestMapping("/api/room/list")
    public RespJson listRoomWithPage(ApiConditionDTO conditionDTO){
        IPage<GameRoom> page = gameRoomService.getCanJoinGameRoomWithPage(conditionDTO);
        return RespUtil.successPageResJson(page);
    }

}
