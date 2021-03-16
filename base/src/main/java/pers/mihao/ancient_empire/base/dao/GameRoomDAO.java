package pers.mihao.ancient_empire.base.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import pers.mihao.ancient_empire.common.dto.ApiConditionDTO;

import java.util.List;
import pers.mihao.ancient_empire.base.entity.GameRoom;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author mihao
 * @since 2021-03-02
 */
@Mapper
public interface GameRoomDAO extends BaseMapper<GameRoom> {

    /**
     * 获取可以加入的游戏房间
     * @param conditionDTO
     * @return
     */
    List<GameRoom> getCanJoinGameRoomWithPage(ApiConditionDTO conditionDTO);

    /**
     * 锁住房间号
     * @param roomId
     * @return
     */
    GameRoom lockRoomById(String roomId);
}
