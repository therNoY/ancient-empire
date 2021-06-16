package pers.mihao.ancient_empire.base.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import pers.mihao.ancient_empire.base.dto.*;
import com.baomidou.mybatisplus.extension.service.IService;
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
     * 创建一个准备好的房间
     * @param reqAddRoomDTO
     * @return
     */
    GameRoom addReadyRoom2Catch(ReqAddRoomDTO reqAddRoomDTO);

    /**
     * 玩家加入房间 返回可以控制的军队
     * @param reqRoomIdDTO
     * @return 控制的军队颜色
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

    /**
     * 离开单位控制
     * @param levelDTO
     * @return
     */
    void levelCtlArmy(RoomArmyLevelDTO levelDTO);
}
