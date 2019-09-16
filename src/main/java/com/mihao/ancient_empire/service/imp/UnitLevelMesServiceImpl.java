package com.mihao.ancient_empire.service.imp;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mihao.ancient_empire.constant.RedisKey;
import com.mihao.ancient_empire.dto.admin_dto.RespUnitLevelDto;
import com.mihao.ancient_empire.entity.UnitLevelMes;
import com.mihao.ancient_empire.dao.UnitLevelMesDao;
import com.mihao.ancient_empire.service.UnitLevelMesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 单位等级信息表 服务实现类
 * </p>
 *
 * @author mihao
 * @since 2019-08-11
 */
@Service
public class UnitLevelMesServiceImpl extends ServiceImpl<UnitLevelMesDao, UnitLevelMes> implements UnitLevelMesService {

    @Autowired
    UnitLevelMesDao unitLevelMesDao;

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
    @Cacheable(RedisKey.UNIT_LEVEL_MES)
    public UnitLevelMes getUnitLevelMes(String type, Integer level) {
        return unitLevelMesDao.getUnitLevelMes(type, level);
    }

    @Override
    public void insert(UnitLevelMes unitLevelMes) {
        unitLevelMesDao.insert(unitLevelMes);
    }
}
