package pers.mihao.ancient_empire.base.dao;

import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.dto.ReqUserTemplateDTO;
import pers.mihao.ancient_empire.base.dto.TemplateIdDTO;
import pers.mihao.ancient_empire.base.entity.UnitMes;
import pers.mihao.ancient_empire.base.entity.UserTemplate;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author mihao
 * @since 2020-09-22
 */
public interface UserTemplateDAO extends BaseMapper<UserTemplate> {


    /**
     * 查询用户的模板的单位
     * @param templateIdDTO
     * @return
     */
    List<UnitMes> getUserAllTempUnit(TemplateIdDTO templateIdDTO);

    /**
     *
     * @param userId
     * @param id
     * @param status
     */
    void updateUserTemplateStatusById(Integer userId, String id, int status);
}
