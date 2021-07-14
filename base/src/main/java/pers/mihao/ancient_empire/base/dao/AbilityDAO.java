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

    /**
     * 获取单位能力
     *
     * @param id
     * @return
     */
    List<Ability> getUnitAbilityList(Integer id);

}
