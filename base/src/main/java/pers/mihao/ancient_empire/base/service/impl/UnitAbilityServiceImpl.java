package pers.mihao.ancient_empire.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.mihao.ancient_empire.base.dao.UnitAbilityDAO;
import pers.mihao.ancient_empire.base.entity.Ability;
import pers.mihao.ancient_empire.base.entity.UnitAbility;
import pers.mihao.ancient_empire.base.service.UnitAbilityService;
import pers.mihao.ancient_empire.common.annotation.redis.NotGenerator;

import java.util.List;

import static pers.mihao.ancient_empire.common.constant.CacheKey.UNIT_ABILITY;

/**
 * <p>
 * 能力信息表 服务实现类
 * </p>
 *
 * @author mihao
 * @since 2019-08-31
 */
@Service
public class UnitAbilityServiceImpl extends ServiceImpl<UnitAbilityDAO, UnitAbility> implements UnitAbilityService {

    @Autowired
    UnitAbilityDAO unitAbilityDAO;


    @Override
    @Transactional
    @CacheEvict(UNIT_ABILITY)
    public void updateUnitAbility(Integer unitId, @NotGenerator List<Ability> abilityInfo) {
        QueryWrapper<UnitAbility> wrapper = new QueryWrapper<>();
        wrapper.eq("unit_id", unitId);
        unitAbilityDAO.delete(wrapper);

        UnitAbility unitAbility;
        for (Ability ability : abilityInfo) {
            unitAbility = new UnitAbility();
            unitAbility.setUnitId(unitId);
            unitAbility.setAbilityId(ability.getId());
            unitAbilityDAO.insert(unitAbility);
        }
    }
}
