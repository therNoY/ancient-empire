package com.mihao.ancient_empire.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mihao.ancient_empire.dto.admin_dto.RespUnitLevelDto;
import com.mihao.ancient_empire.entity.UnitLevelMes;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 单位等级信息表 Mapper 接口
 * </p>
 *
 * @author mihao
 * @since 2019-08-11
 */
public interface UnitLevelMesDao extends BaseMapper<UnitLevelMes> {

    @Select("SELECT um.type, ulm.* from unit_mes um left JOIN unit_level_mes ulm on um.id = ulm.unit_id ")
    IPage<RespUnitLevelDto> getUnitLevelMesList(Page page);

    @Select("SELECT ulm.* from unit_level_mes ulm LEFT JOIN unit_mes um on ulm.unit_id = um.id " +
            "WHERE ulm.`level` = #{level} and um.type = #{type}")
    UnitLevelMes getUnitLevelMes(@Param("type")String type, @Param("level")Integer level);
}
