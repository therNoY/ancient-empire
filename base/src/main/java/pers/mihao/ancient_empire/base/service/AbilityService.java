package pers.mihao.ancient_empire.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
import pers.mihao.ancient_empire.base.entity.Ability;

/**
 * <p>
 * 单位信息表 服务类
 * </p>
 *
 * @author mihao
 * @since 2019-08-31
 */
public interface AbilityService extends IService<Ability> {

    List<Ability> getUnitAbilityList(Integer id);

    List<Ability> getUnitAbilityListByType(String type);
}
