package pers.mihao.ancient_empire.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import pers.mihao.ancient_empire.auth.util.AuthUtil;
import pers.mihao.ancient_empire.base.dao.RegionMesDao;
import pers.mihao.ancient_empire.base.entity.RegionMes;
import pers.mihao.ancient_empire.base.service.RegionMesService;
import pers.mihao.ancient_empire.common.constant.RedisKey;

/**
 * <p>
 * 地形信息表 服务实现类
 * </p>
 *
 * @author mihao
 * @since 2019-08-11
 */
@Service
public class RegionMesServiceImpl extends ServiceImpl<RegionMesDao, RegionMes> implements RegionMesService {

    @Autowired
    RegionMesDao regionMesDao;

    @Override
    public IPage<RegionMes> getList(Page<RegionMes> page) {
        return regionMesDao.selectPage(page, null);
    }

    @Override
    public void saveUnitMes(RegionMes regionMes) {

        if (regionMes.getId() == null) {
            // 新增地形
            regionMes.setCreateUserId(AuthUtil.getAuthId());
            regionMesDao.insert(regionMes);
        }else {
            regionMesDao.updateById(regionMes);
        }
    }

    @Override
    @Cacheable(RedisKey.ENABLE_REGION)
    public List<RegionMes> getEnableRegionByUserId(Integer id) {
        // 目前没有开启个性化功能 暂不开启
        QueryWrapper wrapper = new QueryWrapper<>().eq("enable", "1");
        List<RegionMes> regionMesList = regionMesDao.selectList(wrapper);
        return regionMesList;
    }

    /**
     * 获取region by type
     * @param type
     * @return
     */
    @Override
    @Cacheable(RedisKey.REGION_MES)
    public RegionMes getRegionByType(String type) {
        QueryWrapper wrapper = new QueryWrapper<>().eq("type", type);
        RegionMes regionMes = regionMesDao.selectOne(wrapper);
        return regionMes;
    }
}
