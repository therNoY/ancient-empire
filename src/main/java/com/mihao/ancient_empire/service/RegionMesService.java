package com.mihao.ancient_empire.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mihao.ancient_empire.entity.RegionMes;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 地形信息表 服务类
 * </p>
 *
 * @author mihao
 * @since 2019-08-11
 */
public interface RegionMesService extends IService<RegionMes> {

    IPage<RegionMes> getList(Page<RegionMes> page);

    void saveUnitMes(RegionMes regionMes);

    List<RegionMes> getEnableRegionByUserId(Integer id);

    RegionMes getRegionByType(String type);
}
