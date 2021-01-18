package pers.mihao.ancient_empire.base.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.bo.UnitInfo;
import pers.mihao.ancient_empire.base.dto.ReqGetUnitMesDTO;
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

    /**
     * 获取我的单位信息的
     * @param reqGetUnitMesDTO
     * @return
     */
    IPage<UnitMes> selectUnitMesWithPage(ReqGetUnitMesDTO reqGetUnitMesDTO);

    /**
     * 保存单位信息
     * @param unitMes
     */
    void saveUnitMes(UnitMes unitMes);

    /**
     *
     * 获取可用单位
     * @param tempId
     * @return
     */
    List<UnitMes> getEnableUnitByTempId(String tempId);

    @Deprecated
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
    List<UnitMes> getCanBuyUnit(Integer templateId);

    UnitMes getMaxCheapUnit();

    /**
     * 获取用户创建的所有单位
     * @param userId
     * @return
     */
    List<UnitMes> getUnitListByCreateUser(Integer userId);

    /**
     * 获取管理员启动的单位 即是默认单位
     * @return
     */
    List<UnitMes> getDefaultUnitList();

    /**
     * 更新单位信息
     * @param baseInfo
     */
    void updateInfoById(UnitMes baseInfo);
}
