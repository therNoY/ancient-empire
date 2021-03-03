package pers.mihao.ancient_empire.base.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import pers.mihao.ancient_empire.base.entity.GameRoom;
import com.baomidou.mybatisplus.extension.service.IService;
import pers.mihao.ancient_empire.common.dto.ApiConditionDTO;

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
}
