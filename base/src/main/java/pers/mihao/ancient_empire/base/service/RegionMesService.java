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

    /**
     * 获取模板的可用地形
     * @param id
     * @return
     */
    List<RegionMes> getEnableRegionByTempId(Integer id);


    /**
     * 根据地形的type 获取地形信息从本地缓存中获取
     * @param type
     * @return
     */
    RegionMes getRegionByTypeFromLocalCatch(String type);


    /**
     * 根据地形的type 获取地形信息
     * @param type
     * @return
     */
    RegionMes getRegionByType(String type);
}
