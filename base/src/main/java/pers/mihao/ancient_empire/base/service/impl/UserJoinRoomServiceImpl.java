package pers.mihao.ancient_empire.base.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.mihao.ancient_empire.base.dao.UserJoinRoomDAO;
import pers.mihao.ancient_empire.base.entity.UserJoinRoom;
import pers.mihao.ancient_empire.base.service.UserJoinRoomService;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author mihao
 * @since 2021-03-02
 */
@Service
public class UserJoinRoomServiceImpl extends ServiceImpl<UserJoinRoomDAO, UserJoinRoom> implements UserJoinRoomService {

    @Autowired
    UserJoinRoomDAO userJoinRoomDAO;


}
