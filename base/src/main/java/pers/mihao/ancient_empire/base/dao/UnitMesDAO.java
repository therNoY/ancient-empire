package pers.mihao.ancient_empire.base.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import pers.mihao.ancient_empire.base.dto.ReqGetUnitMesDTO;
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

    /**
     * 获取用户的单位分页
     * @param reqGetUnitMesDTO
     * @return
     */
    List<UnitMes> selectUnitMesWithPage(ReqGetUnitMesDTO reqGetUnitMesDTO);

    /**
     * 更新单位
     * @param baseInfo
     */
    void updateInfoById(UnitMes baseInfo);
}
