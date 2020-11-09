package pers.mihao.ancient_empire.base.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import pers.mihao.ancient_empire.base.entity.UnitMes;

import java.util.List;

/**
 * <p>
 * 单位信息表 Mapper 接口
 * </p>
 *
 * @author mihao
 * @since 2019-08-11
 */
public interface UnitMesDAO extends BaseMapper<UnitMes> {

    /**
     * 获取可以购买的单位
     * @param templateId
     * @return
     */
    List<UnitMes> selectCanTradeUnit(Integer templateId);

    /**
     * 获取所有可用单位
     * @param tempId
     * @return
     */
    List<UnitMes> getEnableUnitByTempId(String tempId);
}
