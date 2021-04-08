package pers.mihao.ancient_empire.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.mihao.ancient_empire.base.entity.UserJoinRoom;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author mihao
 * @since 2021-03-02
 */
public interface UserJoinRoomService extends IService<UserJoinRoom> {


    /**
     * 获取当前房间的玩家
     * @param roomId
     * @return
     */
    List<UserJoinRoom> getUserByRoomId(String roomId);

}
