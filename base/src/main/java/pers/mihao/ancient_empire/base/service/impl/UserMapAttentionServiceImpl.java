package pers.mihao.ancient_empire.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.mihao.ancient_empire.base.dao.UserMapAttentionDao;
import pers.mihao.ancient_empire.base.dto.UserMapIdDTO;
import pers.mihao.ancient_empire.base.entity.UserMap;
import pers.mihao.ancient_empire.base.entity.UserMapAttention;
import pers.mihao.ancient_empire.base.service.UserMapAttentionService;
import pers.mihao.ancient_empire.base.service.UserMapService;
import pers.mihao.ancient_empire.common.mybatis_plus_helper.ComplexKeyServiceImpl;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author mihao
 * @since 2021-04-26
 */
@Service
public class UserMapAttentionServiceImpl extends ComplexKeyServiceImpl<UserMapAttentionDao, UserMapAttention> implements UserMapAttentionService {

    @Autowired
    UserMapAttentionDao userMapAttentionDao;

    @Autowired
    UserMapService userMapService;

    @Override
    public void updateMaxVersion(UserMapIdDTO id) {
        UserMap userMap = userMapService.getUserMapById(id.getMapId());
        if (userMap != null) {
            UserMap newVersion = userMapService.getMaxVersionMapByMapType(userMap.getMapType());
            QueryWrapper<UserMapAttention> wrapper = new QueryWrapper<>();
            wrapper.eq("user_id", id.getUserId());
            wrapper.eq("map_id", id.getMapId());
            UserMapAttention attention = userMapAttentionDao.selectOne(wrapper);
            remove(wrapper);

            attention.setMapId(newVersion.getUuid());
            saveOrUpdate(attention);
        }
    }
}
