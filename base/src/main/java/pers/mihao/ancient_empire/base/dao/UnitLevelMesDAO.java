package pers.mihao.ancient_empire.base.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import pers.mihao.ancient_empire.base.dto.RespUnitLevelDto;
import pers.mihao.ancient_empire.base.entity.UnitLevelMes;

import java.util.List;

/**
 * <p>
 * 单位等级信息表 Mapper 接口
 * </p>
 *
 * @author mihao
 * @since 2019-08-11
 */
public interface UnitLevelMesDAO extends BaseMapper<UnitLevelMes> {


    /**
     * 获取单位的等级信息
     * @param id
     * @param level
     * @return
     */
    UnitLevelMes getUnitLevelMes(@Param("id")Integer id, @Param("level")Integer level);


    /**
     * 获取模板所有单位的等级信息
     * @param tempId
     * @return
     */
    List<UnitLevelMes> getAllUnitLevelInfoByTempId(Integer tempId);

    /**
     * 获取单位的等级信息
     * @param tempId
     * @return
     */
    List<UnitLevelMes> getUnitLevelInfoById(Integer tempId);
}
