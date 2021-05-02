package pers.mihao.ancient_empire.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.mihao.ancient_empire.base.entity.Ability;
import pers.mihao.ancient_empire.base.entity.UnitAbility;
import pers.mihao.ancient_empire.common.annotation.redis.NotGenerator;

import java.util.List;

/**
 * <p>
 * 能力信息表 服务类
 * </p>
 *
 * @author mihao
 * @since 2019-08-31
 */
public interface UnitAbilityService extends IService<UnitAbility> {

    /**
     * 更新单位能力
     * @param unitId
     * @param abilityInfo
     */
    void updateUnitAbility(Integer unitId, @NotGenerator List<Ability> abilityInfo);
}
