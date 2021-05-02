package pers.mihao.ancient_empire.base.service.imp;

import pers.mihao.ancient_empire.base.entity.UserMapAttention;
import pers.mihao.ancient_empire.base.dao.UserMapAttentionDao;
import pers.mihao.ancient_empire.base.service.UserMapAttentionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pers.mihao.ancient_empire.common.mybatis_plus_helper.ComplexKeyService;
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

}
