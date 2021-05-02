package pers.mihao.ancient_empire.base.service.impl;

import pers.mihao.ancient_empire.base.entity.RegionTemplateRelation;
import pers.mihao.ancient_empire.base.dao.RegionTemplateRelationDAO;
import pers.mihao.ancient_empire.base.service.RegionTemplateRelationService;
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
public class RegionTemplateRelationServiceImpl extends
    ComplexKeyServiceImpl<RegionTemplateRelationDAO, RegionTemplateRelation> implements RegionTemplateRelationService {

}
