package pers.mihao.ancient_empire.base.service;

import java.util.List;
import java.util.Map;
import pers.mihao.ancient_empire.base.entity.UnitLevelMes;
import pers.mihao.ancient_empire.common.mybatis_plus_helper.ComplexKeyService;

/**
 * <p>
 * 单位等级信息表 服务类
 * </p>
 *
 * @author mihao
 * @since 2019-08-11
 */
public interface UnitLevelMesService extends ComplexKeyService<UnitLevelMes> {

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

    /**
     * 通过模板获取单位的等级信息
     * @param userId
     * @return
     */
    Map<String, UnitLevelMes> getAllUnitLevelInfoByTempId(Integer userId);

    /**
     * 获取单个单位的等级信息
     * @param userId
     * @return
     */
    List<UnitLevelMes> getUnitLevelInfoById(Integer userId);
}
