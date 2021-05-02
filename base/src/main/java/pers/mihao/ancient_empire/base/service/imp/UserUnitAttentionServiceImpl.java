package pers.mihao.ancient_empire.base.service.imp;

import org.springframework.beans.factory.annotation.Autowired;
import pers.mihao.ancient_empire.base.entity.UserUnitAttention;
import pers.mihao.ancient_empire.base.dao.UserUnitAttentionDao;
import pers.mihao.ancient_empire.base.service.UserUnitAttentionService;
import org.springframework.stereotype.Service;
import pers.mihao.ancient_empire.common.mybatis_plus_helper.ComplexKeyServiceImpl;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author mihao
 * @since 2021-04-29
 */
@Service
public class UserUnitAttentionServiceImpl extends ComplexKeyServiceImpl<UserUnitAttentionDao, UserUnitAttention> implements
    UserUnitAttentionService {

    @Autowired
    UserUnitAttentionDao userUnitAttentionDao;

    @Override
    public void deleteUserDownloadUnit(Integer userId, Integer id) {
        UserUnitAttention userUnitAttention = new UserUnitAttention();
        userUnitAttention.setUserId(userId);
        userUnitAttention.setUnitId(id);
        deleteByPrimaryKey(userUnitAttention);
    }
}
