package pers.mihao.ancient_empire.base.service;

import pers.mihao.ancient_empire.base.dto.ReqSaveUserTemplateDTO;
import pers.mihao.ancient_empire.base.entity.UnitMes;
import pers.mihao.ancient_empire.base.entity.UnitTemplateRelation;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author mihao
 * @since 2020-09-23
 */
public interface UnitTemplateRelationService extends IService<UnitTemplateRelation> {


    /**
     * 保存单位模板关联关系
     * @param reqSaveUserTemplateDTO
     */
    void saveRelation(ReqSaveUserTemplateDTO reqSaveUserTemplateDTO);
}
