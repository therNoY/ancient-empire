package pers.mihao.ancient_empire.base.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import pers.mihao.ancient_empire.base.dao.AbilityDao;
import pers.mihao.ancient_empire.base.entity.Ability;
import pers.mihao.ancient_empire.base.service.AbilityService;
import pers.mihao.ancient_empire.common.constant.RedisKey;

/**
 * <p>
 * 单位信息表 服务实现类
 * </p>
 *
 * @author mihao
 * @since 2019-08-31
 */
@Service
public class AbilityServiceImpl extends ServiceImpl<AbilityDao, Ability> implements AbilityService {


    @Autowired
    AbilityDao abilityDao;

    @Cacheable(RedisKey.UNIT_ABILITY)
    @Override
    public List<Ability> getUnitAbilityList(Integer id) {
        return abilityDao.getUnitAbilityList(id);
    }

    /**
     * 获取单位能力
     * @param type
     * @return
     */
    @Cacheable(RedisKey.UNIT_ABILITY)
    @Override
    public List<Ability> getUnitAbilityListByType(String type) {
        return abilityDao.getUnitAbilityListByType(type);
    }
}
