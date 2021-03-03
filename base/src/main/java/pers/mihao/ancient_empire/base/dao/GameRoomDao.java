package pers.mihao.ancient_empire.base.dao;

import pers.mihao.ancient_empire.base.entity.GameRoom;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import pers.mihao.ancient_empire.common.dto.ApiConditionDTO;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author mihao
 * @since 2021-03-02
 */
public interface GameRoomDao extends BaseMapper<GameRoom> {

    /**
     * 获取可以加入的游戏房间
     * @param conditionDTO
     * @return
     */
    List<GameRoom> getCanJoinGameRoomWithPage(ApiConditionDTO conditionDTO);
}
