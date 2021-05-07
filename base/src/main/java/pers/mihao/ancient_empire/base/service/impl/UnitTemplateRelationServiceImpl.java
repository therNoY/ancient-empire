package pers.mihao.ancient_empire.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import java.util.ArrayList;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import pers.mihao.ancient_empire.base.dto.ReqSaveUserTemplateDTO;
import pers.mihao.ancient_empire.base.entity.UnitTemplateRelation;
import pers.mihao.ancient_empire.base.dao.UnitTemplateRelationDAO;
import pers.mihao.ancient_empire.base.service.UnitTemplateRelationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import pers.mihao.ancient_empire.common.mybatis_plus_helper.ComplexKeyServiceImpl;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author mihao
 * @since 2020-09-23
 */
@Service
public class UnitTemplateRelationServiceImpl extends ComplexKeyServiceImpl<UnitTemplateRelationDAO, UnitTemplateRelation> implements UnitTemplateRelationService {


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveRelation(ReqSaveUserTemplateDTO reqSaveUserTemplateDTO) {
        QueryWrapper<UnitTemplateRelation> wrapper = new QueryWrapper<>();
        wrapper.eq("temp_id", reqSaveUserTemplateDTO.getId());
        remove(wrapper);

        List<UnitTemplateRelation> relations = new ArrayList<>();
        for (Integer unitId : reqSaveUserTemplateDTO.getRelationUnitList()) {
            UnitTemplateRelation relation = new UnitTemplateRelation();
            relation.setTempId(reqSaveUserTemplateDTO.getId());
            relation.setUnitId(unitId);
            relations.add(relation);
        }
        saveBatch(relations);
    }
}
