package pers.mihao.ancient_empire.base.service.impl;

import org.springframework.cache.annotation.Cacheable;
import pers.mihao.ancient_empire.base.entity.UserTemplate;
import pers.mihao.ancient_empire.base.dao.UserTemplateDAO;
import pers.mihao.ancient_empire.base.service.UserTemplateService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pers.mihao.ancient_empire.common.constant.CatchKey;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author mihao
 * @since 2020-09-22
 */
@Service
public class UserTemplateServiceImpl extends ServiceImpl<UserTemplateDAO, UserTemplate> implements UserTemplateService {

    @Cacheable(CatchKey.USER_TEMP)
    @Override
    public UserTemplate selectById(String id) {
        return getById(id);
    }
}
