package pers.mihao.ancient_empire.base.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import pers.mihao.ancient_empire.base.dao.UnitLevelMesDAO;
import pers.mihao.ancient_empire.base.dto.RespUnitLevelDto;
import pers.mihao.ancient_empire.base.entity.UnitLevelMes;
import pers.mihao.ancient_empire.base.service.UnitLevelMesService;
import pers.mihao.ancient_empire.common.constant.CatchKey;

/**
 * <p>
 * 单位等级信息表 服务实现类
 * </p>
 *
 * @author mihao
 * @since 2019-08-11
 */
@Service
public class UnitLevelMesServiceImpl extends ServiceImpl<UnitLevelMesDAO, UnitLevelMes> implements UnitLevelMesService {

    @Autowired
    UnitLevelMesDAO unitLevelMesDao;

    @Override
    public IPage<RespUnitLevelDto> getUnitLevelMesList(Page page) {
        IPage iPage = unitLevelMesDao.getUnitLevelMesList(page);
        return iPage;
    }

    /**
     * 用于修改单位等级信息
     * @param unitLevelMes
     */
    @Override
    public void saveUnitLevelMesList(UnitLevelMes unitLevelMes) {
        unitLevelMesDao.updateById(unitLevelMes);
    }

    @Override
    public int getSpeedByUnit(String type, Integer level) {
        UnitLevelMes unitLevelMes = null;
        if ((unitLevelMes = getUnitLevelMes(type, level)) != null) {
            return unitLevelMes.getSpeed();
        }
        return 0;
    }

    @Override
    @Cacheable(CatchKey.UNIT_LEVEL_MES)
    public UnitLevelMes getUnitLevelMes(String type, Integer level) {
        return unitLevelMesDao.getUnitLevelMes(type, level);
    }

    @Override
    public void insert(UnitLevelMes unitLevelMes) {
        unitLevelMesDao.insert(unitLevelMes);
    }
}
