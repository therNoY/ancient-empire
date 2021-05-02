package pers.mihao.ancient_empire.base.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import pers.mihao.ancient_empire.base.dto.ApiOrderDTO;
import pers.mihao.ancient_empire.base.entity.UnitMes;
import pers.mihao.ancient_empire.base.vo.UnitMesVO;
import pers.mihao.ancient_empire.common.dto.ApiConditionDTO;

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
     * 获取模板所有可用单位
     * @param tempId
     * @return
     */
    List<UnitMes> getEnableUnitByTempId(String tempId);

    /**
     * 获取用户的单位分页
     * @param apiConditionDTO
     * @return
     */
    List<UnitMes> selectUnitMesByCreateUserWithPage(ApiConditionDTO apiConditionDTO);

    /**
     * 获取用户下载单位
     * @param conditionDTO
     * @return
     */
    List<UnitMesVO> getUserDownloadUnitMesWithPage(ApiConditionDTO conditionDTO);

    /**
     * 获取用户可以下载的列表
     * @param orderDTO
     * @return
     */
    List<UnitMesVO> getDownloadAbleUnitMesWithPage(ApiOrderDTO orderDTO);

    /**
     * 获取这个版本的最新版
     * @param type
     * @return
     */
    Integer getMaxVersionByType(String type);
}
