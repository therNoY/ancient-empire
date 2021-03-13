package pers.mihao.ancient_empire.base.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pers.mihao.ancient_empire.base.dto.ReqAddRoomDTO;
import pers.mihao.ancient_empire.base.dto.ReqRoomIdDTO;
import pers.mihao.ancient_empire.base.entity.GameRoom;
import pers.mihao.ancient_empire.base.service.GameRoomService;
import pers.mihao.ancient_empire.base.service.UserJoinRoomService;
import pers.mihao.ancient_empire.common.dto.ApiConditionDTO;
import pers.mihao.ancient_empire.common.util.RespUtil;
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

    @Autowired
    GameRoomService gameRoomService;

    @Autowired
    UserJoinRoomService userJoinRoomService;

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
        boolean res = gameRoomService.playerJoinRoom(reqRoomIdDTO);
        if (res) {
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
        boolean res = gameRoomService.playerLevelRoom(reqRoomIdDTO);
        return RespUtil.successResJson();
    }

}
