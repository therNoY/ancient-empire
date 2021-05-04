package pers.mihao.ancient_empire.base.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.bo.UnitInfo;
import pers.mihao.ancient_empire.base.dto.ApiOrderDTO;
import pers.mihao.ancient_empire.base.dto.ReqGetUnitMesDTO;
import pers.mihao.ancient_empire.base.dto.ReqSaveUnitMesDTO;
import pers.mihao.ancient_empire.base.entity.UnitMes;
import pers.mihao.ancient_empire.base.vo.UnitMesVO;
import pers.mihao.ancient_empire.common.dto.ApiConditionDTO;

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
     * @param apiConditionDTO
     * @return
     */
    IPage<UnitMes> selectUnitMesByCreateUserWithPage(ApiConditionDTO apiConditionDTO);

    /**
     * 保存单位信息
     * @param unitMes
     */
    void saveUnitMes(UnitMes unitMes);

    /**
     * 根据Id获取单位基础信息
     * @param id
     * @return
     */
    UnitMes getUnitMesById(Integer id);

    /**
     *
     * 根据模板获取可用单位
     * @param tempId
     * @return
     */
    List<UnitMes> getEnableUnitByTempId(String tempId);

    /**
     * 通过单位和等级获取单位详情
     * @param id
     * @param level
     * @return
     */
    UnitInfo getUnitInfo(Integer id, Integer level);

    /**
     * 根据模板获取用户可以购买的单位
     * @param templateId
     * @return
     */
    List<UnitMes> getCanBuyUnit(Integer templateId);

    /**
     * 获取用户创建的所有单位
     * @param userId
     * @return
     */
    List<UnitMes> getUserEnableUnitList(Integer userId);

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

    /**
     * 根据类型获取单位信息的草稿版本
     * @param type
     * @return
     */
    UnitMes getDraftVersionUnitMes(String type);

    /**
     * 获取用户下载单位
     * @param conditionDTO
     * @return
     */
    IPage<UnitMesVO> getUserDownloadUnitMesWithPage(ApiConditionDTO conditionDTO);

    /**
     * 获取可以下载的单位列表
     * @param orderDTO
     * @return
     */
    IPage<UnitMesVO> getDownloadAbleUnitMesWithPage(ApiOrderDTO orderDTO);

    /**
     * 批量更新某一类型的单位
     * @param type
     * @param delete
     */
    void updateUnitStatusByType(String type, Integer delete);

    /**
     * 获取单位的最新版本信息
     * @param type
     * @return
     */
    UnitMes getMaxVersionUnitByType(String type);

    /**
     * 根据类型删除缓存
     * @param type
     */
    void delMaxVersionCatch(String type);

    /**
     * 保存新的单位
     * @param reqSaveUnitMesDTO
     */
    void saveUnitInfo(ReqSaveUnitMesDTO reqSaveUnitMesDTO);
}
