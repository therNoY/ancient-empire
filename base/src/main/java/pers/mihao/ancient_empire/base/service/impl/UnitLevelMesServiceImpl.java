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
import pers.mihao.ancient_empire.common.constant.BaseConstant;
import pers.mihao.ancient_empire.common.constant.CatchKey;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
     *
     * @param unitLevelMes
     */
    @Override
    public void saveUnitLevelMesList(UnitLevelMes unitLevelMes) {
        unitLevelMesDao.updateById(unitLevelMes);
    }


    @Override
    @Cacheable(CatchKey.UNIT_LEVEL_MES)
    public UnitLevelMes getUnitLevelMes(Integer id, Integer level) {
        return unitLevelMesDao.getUnitLevelMes(id, level);
    }

    @Override
    public void insert(UnitLevelMes unitLevelMes) {
        unitLevelMesDao.insert(unitLevelMes);
    }

    @Override
    public Map<String, UnitLevelMes> getAllUnitLevelInfoByTempId(Integer userId) {
        List<UnitLevelMes> levelMesList = unitLevelMesDao.getAllUnitLevelInfoByTempId(userId);
        Map<String, UnitLevelMes> levelMesMap = levelMesList.stream()
                .collect(Collectors.toMap(l -> l.getUnitId() + BaseConstant.COMMA + l.getLevel(), Function.identity()));
        return levelMesMap;
    }

    @Override
    public List<UnitLevelMes> getUnitLevelInfoById(Integer userId) {
        List<UnitLevelMes> levelMesList = unitLevelMesDao.getUnitLevelInfoById(userId);
        return levelMesList;
    }

}
