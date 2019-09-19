package com.mihao.ancient_empire.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.mihao.ancient_empire.dto.ReqUnitInfoDto;
import com.mihao.ancient_empire.dto.UnitInfo;
import com.mihao.ancient_empire.entity.UnitMes;

import java.util.List;

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

    UnitInfo getUnitInfo(String type, Integer level);

    List<UnitInfo> getUnitInfoList(boolean hasLoad);
}
