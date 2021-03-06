package pers.mihao.ancient_empire.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import pers.mihao.ancient_empire.auth.util.LoginUserHolder;
import pers.mihao.ancient_empire.base.dao.RegionMesDAO;
import pers.mihao.ancient_empire.base.entity.RegionMes;
import pers.mihao.ancient_empire.base.service.RegionMesService;
import pers.mihao.ancient_empire.common.constant.CommonConstant;
import pers.mihao.ancient_empire.common.constant.CacheKey;

/**
 * <p>
 * 地形信息表 服务实现类
 * </p>
 *
 * @author mihao
 * @since 2019-08-11
 */
@Service
public class RegionMesServiceImpl extends ServiceImpl<RegionMesDAO, RegionMes> implements RegionMesService {

    @Autowired
    RegionMesDAO regionMesDao;

    private Map<String, RegionMes> regionMesMap = new HashMap<>(16);

    @Override
    public IPage<RegionMes> getList(Page<RegionMes> page) {
        return regionMesDao.selectPage(page, null);
    }

    @Override
    public void saveUnitMes(RegionMes regionMes) {

        if (regionMes.getId() == null) {
            // 新增地形
            regionMes.setCreateUserId(LoginUserHolder.getUserId());
            regionMesDao.insert(regionMes);
        }else {
            regionMesDao.updateById(regionMes);
        }
    }

    @Override
    @Cacheable(CacheKey.ENABLE_REGION)
    public List<RegionMes> getEnableRegionByTempId(Integer id) {
        // 获取全部地形
        QueryWrapper wrapper = new QueryWrapper<>().eq("enable", CommonConstant.YES);
        List<RegionMes> regionMesList = regionMesDao.selectList(wrapper);
        return regionMesList;
    }

    /**
     * 获取region by type
     * @param type
     * @return
     */
    @Override
    public RegionMes getRegionByTypeFromLocalCatch(String type) {
        RegionMes regionMes = regionMesMap.get(type);
        if (regionMes == null) {
            regionMes = getRegionByType(type);
            regionMesMap.put(type, regionMes);
        }
        return regionMes;
    }

    /**
     * 获取region by type
     * @param type
     * @return
     */
    @Override
    @Cacheable(CacheKey.REGION_MES)
    public RegionMes getRegionByType(String type) {
        QueryWrapper wrapper = new QueryWrapper<>().eq("type", type);
        RegionMes regionMes = regionMesDao.selectOne(wrapper);
        return regionMes;
    }
}
