package com.mihao.ancient_empire.dao;

import com.mihao.ancient_empire.entity.Ability;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

/**
 * <p>
 * 单位信息表 Mapper 接口
 * </p>
 *
 * @author mihao
 * @since 2019-08-31
 */
public interface AbilityDao extends BaseMapper<Ability> {

    @Select("SELECT a.* FROM `ability` a , unit_ability ua , unit_mes u " +
            "WHERE u.id = ua.unit_id and ua.ability_id = a.id and u.id = #{id}")
    List<Ability> getUnitAbilityList(Integer id);
}
