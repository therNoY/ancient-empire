package pers.mihao.ancient_empire.base.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import pers.mihao.ancient_empire.base.dto.ArmyConfig;
import pers.mihao.ancient_empire.base.dto.ReqAddRoomDTO;
import com.baomidou.mybatisplus.extension.service.IService;
import pers.mihao.ancient_empire.base.dto.ReqRoomIdDTO;
import pers.mihao.ancient_empire.base.dto.RoomArmyChangeDTO;
import pers.mihao.ancient_empire.base.entity.GameRoom;
import pers.mihao.ancient_empire.common.dto.ApiConditionDTO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author mihao
 * @since 2021-03-02
 */
public interface GameRoomService extends IService<GameRoom> {

    /**
     * 获取可以分页的游戏房间
     * @param conditionDTO
     * @return
     */
    IPage<GameRoom> getCanJoinGameRoomWithPage(ApiConditionDTO conditionDTO);

    /**
     * 创建房间
     * @param reqAddRoomDTO
     * @return
     */
    GameRoom addRoomAndJoinRoomOwner(ReqAddRoomDTO reqAddRoomDTO);

    /**
     * 玩家加入房间
     * @param reqRoomIdDTO
     * @return
     */
    String playerJoinRoom(ReqRoomIdDTO reqRoomIdDTO);


    /**
     * 玩家离开房间
     * @param id
     */
    void userLevelRoom(Integer id);

    /**
     * 玩家在房间内改变控制军队
     * @param armyChangeDTO
     * @return
     */
    String changeCtlArmy(RoomArmyChangeDTO armyChangeDTO);

    /**
     * 获取当前的军队配置
     * @param id
     * @return
     */
    List<ArmyConfig> getCurrentArmyConfigByRoomId(String id);
}
