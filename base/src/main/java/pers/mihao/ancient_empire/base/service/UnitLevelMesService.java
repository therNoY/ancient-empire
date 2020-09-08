package pers.mihao.ancient_empire.base.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import pers.mihao.ancient_empire.base.dto.RespUnitLevelDto;
import pers.mihao.ancient_empire.base.entity.UnitLevelMes;

/**
 * <p>
 * 单位等级信息表 服务类
 * </p>
 *
 * @author mihao
 * @since 2019-08-11
 */
public interface UnitLevelMesService extends IService<UnitLevelMes> {

    IPage<RespUnitLevelDto> getUnitLevelMesList(Page page);

    void saveUnitLevelMesList(UnitLevelMes unitLevelMes);

    int getSpeedByUnit(String type, Integer level);

    UnitLevelMes getUnitLevelMes(String type, Integer level);

    void insert(UnitLevelMes unitLevelMes);
}
