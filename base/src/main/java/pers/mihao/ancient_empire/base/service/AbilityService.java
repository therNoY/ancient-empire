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

    /**
     * 通过ID 获取单位能力
     * @param id
     * @return
     */
    List<Ability> getUnitAbilityList(Integer id);

    /**
     * 通过类型获取 弃用
     * @param type
     * @return
     */
    @Deprecated
    List<Ability> getUnitAbilityListByType(String type);
}
