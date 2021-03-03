package pers.mihao.ancient_empire.base.service.imp;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import pers.mihao.ancient_empire.base.entity.GameRoom;
import pers.mihao.ancient_empire.base.dao.GameRoomDao;
import pers.mihao.ancient_empire.base.service.GameRoomService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pers.mihao.ancient_empire.base.util.IPageHelper;
import pers.mihao.ancient_empire.common.dto.ApiConditionDTO;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author mihao
 * @since 2021-03-02
 */
@Service
public class GameRoomServiceImpl extends ServiceImpl<GameRoomDao, GameRoom> implements GameRoomService {

    @Autowired
    GameRoomDao gameRoomDao;

    @Override
    public IPage<GameRoom> getCanJoinGameRoomWithPage(ApiConditionDTO conditionDTO) {
        List<GameRoom> gameRooms = gameRoomDao.getCanJoinGameRoomWithPage(conditionDTO);
        return IPageHelper.toPage(gameRooms, conditionDTO);
    }
}
