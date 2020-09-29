package pers.mihao.ancient_empire.base.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import pers.mihao.ancient_empire.base.dto.RespUnitLevelDto;
import pers.mihao.ancient_empire.base.entity.UnitLevelMes;

/**
 * <p>
 * 单位等级信息表 Mapper 接口
 * </p>
 *
 * @author mihao
 * @since 2019-08-11
 */
public interface UnitLevelMesDAO extends BaseMapper<UnitLevelMes> {

    @Select("SELECT um.type, ulm.* from unit_mes um left JOIN unit_level_mes ulm on um.id = ulm.unit_id ")
    IPage<RespUnitLevelDto> getUnitLevelMesList(Page page);

    @Select("SELECT ulm.* from unit_level_mes ulm LEFT JOIN unit_mes um on ulm.unit_id = um.id " +
            "WHERE ulm.`level` = #{level} and um.type = #{type}")
    UnitLevelMes getUnitLevelMes(@Param("type")String type, @Param("level")Integer level);
}
