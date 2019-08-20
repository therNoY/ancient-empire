package com.mihao.ancient_empire.service.imp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mihao.ancient_empire.constant.RedisKey;
import com.mihao.ancient_empire.entity.RegionMes;
import com.mihao.ancient_empire.dao.RegionMesDao;
import com.mihao.ancient_empire.service.RegionMesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mihao.ancient_empire.util.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
