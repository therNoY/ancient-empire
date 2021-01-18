package pers.mihao.ancient_empire.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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


}
