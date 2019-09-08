package com.mihao.ancient_empire.service;

import com.mihao.ancient_empire.entity.Ability;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

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
