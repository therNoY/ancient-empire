package pers.mihao.ancient_empire.base.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Select;
import pers.mihao.ancient_empire.base.entity.Ability;

/**
 * <p>
 * 单位信息表 Mapper 接口
 * </p>
 *
 * @author mihao
 * @since 2019-08-31
 */
public interface AbilityDAO extends BaseMapper<Ability> {

    @Select("SELECT a.* FROM `ability` a , unit_ability ua , unit_mes u " +
            "WHERE u.id = ua.unit_id and ua.ability_id = a.id and u.id = #{id}")
    List<Ability> getUnitAbilityList(Integer id);

    @Select("SELECT a.* FROM `ability` a , unit_ability ua , unit_mes u " +
            "WHERE u.id = ua.unit_id and ua.ability_id = a.id and u.type = #{type}")
    List<Ability> getUnitAbilityListByType(String type);
}
