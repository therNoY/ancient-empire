package com.mihao.ancient_empire.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mihao.ancient_empire.dto.ReqPageDto;
import com.mihao.ancient_empire.dto.admin_dto.RespUnitLevelDto;
import com.mihao.ancient_empire.entity.UnitLevelMes;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

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
}
