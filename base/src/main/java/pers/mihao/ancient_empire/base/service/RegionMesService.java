package pers.mihao.ancient_empire.base.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
import pers.mihao.ancient_empire.base.entity.RegionMes;

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

    /**
     * 根据地形的type 获取地形信息
     * @param type
     * @return
     */
    RegionMes getRegionByType(String type);
}
