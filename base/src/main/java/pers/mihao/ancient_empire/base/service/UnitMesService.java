package pers.mihao.ancient_empire.base.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
import pers.mihao.ancient_empire.base.bo.UnitInfo;
import pers.mihao.ancient_empire.base.entity.UnitMes;

/**
 * <p>
 * 单位信息表 服务类
 * </p>
 *
 * @author mihao
 * @since 2019-08-11
 */
public interface UnitMesService extends IService<UnitMes> {

    IPage<UnitMes> getList(Page<UnitMes> page);

    void saveUnitMes(UnitMes unitMes);

    List<UnitMes> getEnableUnitByUserId(Integer id);

    UnitMes getByType(String type);

    /**
     * 通过单位和等级获取单位详情
     * @param id
     * @param level
     * @return
     */
    UnitInfo getUnitInfo(String id, Integer level);

    /**
     * 根据模板获取用户可以购买的单位
     * @param hasLoad
     * @return
     */
    List<UnitMes> getUnitInfoList(Integer templateId);

    UnitMes getMaxCheapUnit();

    List<UnitMes> getEnableBuyUnit();
}
