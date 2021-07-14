package pers.mihao.ancient_empire.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import pers.mihao.ancient_empire.base.dao.UnitTransferDAO;
import pers.mihao.ancient_empire.base.entity.UnitTransfer;
import pers.mihao.ancient_empire.base.service.UnitTransferService;
import pers.mihao.ancient_empire.common.constant.CacheKey;
import pers.mihao.ancient_empire.common.mybatis_plus_helper.ComplexKeyServiceImpl;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author mihao
 * @since 2020-10-24
 */
@Service
public class UnitTransferServiceImpl extends ComplexKeyServiceImpl<UnitTransferDAO, UnitTransfer> implements
    UnitTransferService {

    @Autowired
    UnitTransferDAO unitTransferDAO;

    @Cacheable(CacheKey.UNIT_TRANSFER)
    @Override
    public List<UnitTransfer> getTransferByUnitId(Integer unitId) {
        QueryWrapper<UnitTransfer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("unit_id", unitId)
            .eq("use", 1)
            .orderBy(true, true, "order");
        return unitTransferDAO.selectList(queryWrapper);
    }
}
