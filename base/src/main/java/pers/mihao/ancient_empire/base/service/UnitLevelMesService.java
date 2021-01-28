package pers.mihao.ancient_empire.base.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import pers.mihao.ancient_empire.base.dto.RespUnitLevelDto;
import pers.mihao.ancient_empire.base.entity.UnitLevelMes;

import java.util.List;
import java.util.Map;

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

    /**
     * 保存单位等级信息
     * @param unitLevelMes
     */
    void saveUnitLevelMesList(UnitLevelMes unitLevelMes);

    /**
     * 根据单位的id 和等级获取单位的信息
     * @param id 单位id
     * @param level 单位等级
     * @return
     */
    UnitLevelMes getUnitLevelMes(Integer id, Integer level);

    void insert(UnitLevelMes unitLevelMes);

    /**
     * 通过模板获取单位的等级信息
     * @param userId
     * @return
     */
    Map<String, UnitLevelMes> getAllUnitLevelInfoByTempId(Integer userId);
}
