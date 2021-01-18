package pers.mihao.ancient_empire.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import pers.mihao.ancient_empire.base.entity.UserTempAttention;
import pers.mihao.ancient_empire.base.dao.UserTempAttentionDAO;
import pers.mihao.ancient_empire.base.service.UserTempAttentionService;
import org.springframework.stereotype.Service;
import pers.mihao.ancient_empire.common.mybatis_plus_helper.ComplexKeyServiceImpl;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author mihao
 * @since 2020-09-22
 */
@Service
public class UserTempAttentionServiceImpl extends ComplexKeyServiceImpl<UserTempAttentionDAO, UserTempAttention> implements UserTempAttentionService {


    @Override
    public void removeUserAttention(Integer userId, String id) {
        QueryWrapper<UserTempAttention> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.eq("template_id", id);
        this.remove(wrapper);
    }

}
