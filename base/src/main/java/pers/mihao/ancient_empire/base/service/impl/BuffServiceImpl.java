package pers.mihao.ancient_empire.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import pers.mihao.ancient_empire.base.dao.BuffDAO;
import pers.mihao.ancient_empire.base.entity.Buff;
import pers.mihao.ancient_empire.base.service.BuffService;
import pers.mihao.ancient_empire.common.constant.CacheKey;

/**
 * <p>
 * buff信息表 服务实现类
 * </p>
 *
 * @author mihao
 * @since 2019-08-31
 */
@Service
public class BuffServiceImpl extends ServiceImpl<BuffDAO, Buff> implements BuffService {

    @Override
    @Cacheable(CacheKey.BUFF)
    public Buff getByType(String type) {
        QueryWrapper<Buff> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", type);
        return getOne(queryWrapper);
    }
}
