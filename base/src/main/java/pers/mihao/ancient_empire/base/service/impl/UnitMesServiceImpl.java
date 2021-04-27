package pers.mihao.ancient_empire.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.mihao.ancient_empire.auth.util.AuthUtil;
import pers.mihao.ancient_empire.base.bo.UnitInfo;
import pers.mihao.ancient_empire.base.dao.UnitMesDAO;
import pers.mihao.ancient_empire.base.entity.Ability;
import pers.mihao.ancient_empire.base.entity.UnitLevelMes;
import pers.mihao.ancient_empire.base.entity.UnitMes;
import pers.mihao.ancient_empire.base.service.AbilityService;
import pers.mihao.ancient_empire.base.service.UnitLevelMesService;
import pers.mihao.ancient_empire.base.service.UnitMesService;
import pers.mihao.ancient_empire.base.util.IPageHelper;
import pers.mihao.ancient_empire.common.constant.CommonConstant;
import pers.mihao.ancient_empire.common.constant.CatchKey;
import pers.mihao.ancient_empire.common.dto.ApiConditionDTO;
import pers.mihao.ancient_empire.common.mybatis_plus_helper.ComplexKeyServiceImpl;
import pers.mihao.ancient_empire.common.vo.AncientEmpireException;

/**
 * <p>
 * 单位信息表 服务实现类
 * </p>
 *
 * @author mihao
 * @since 2019-08-11
 */
@Service
public class UnitMesServiceImpl extends ComplexKeyServiceImpl<UnitMesDAO, UnitMes> implements UnitMesService {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UnitMesDAO unitMesDao;
    @Autowired
    UnitLevelMesService unitLevelMesService;
    @Autowired
    AbilityService abilityService;

    /**
     * 获取所有单位信息
     *
     * @param page
     * @return
     */
    @Override
    public IPage<UnitMes> selectUnitMesWithPage(ApiConditionDTO apiConditionDTO) {
        List<UnitMes> unitMes = unitMesDao.selectUnitMesWithPage(apiConditionDTO);
        return IPageHelper.toPage(unitMes, apiConditionDTO);
    }

    /**
     * 更新单位信息
     *
     * @param unitMes
     */
    @Override
    @Transactional
    public void saveUnitMes(UnitMes unitMes) {
        if (unitMes.getId() != null) {
            unitMes.setCreateUserId(AuthUtil.getUserId());
            unitMesDao.updateById(unitMes);
        } else {
            unitMesDao.insert(unitMes);

            if (unitMes.getId() != null) {
                for (int i = 0; i < 4; i++) {
                    UnitLevelMes unitLevelMes = new UnitLevelMes();
                    unitLevelMes.setUnitId(unitMes.getId());
                    unitLevelMes.setLevel(i);
                    unitLevelMesService.saveOrUpdate(unitLevelMes);
                }
            } else {
                log.error("没有获取到插入的主键");
                throw new AncientEmpireException(500);
            }

        }
    }

    /**
     * 根据创建者的用户获取 可用的Unit
     * 获取所有可用单位信息
     *
     * @return
     */
    @Override
    @Cacheable(value = CatchKey.ENABLE_UNIT)
    public List<UnitMes> getEnableUnitByTempId(String tempId) {
        log.info(">>>>用户从数据库中获取可用单位<<<<");
        List<UnitMes> unitMes = unitMesDao.getEnableUnitByTempId(tempId);
        return unitMes;
    }



    /**
     * 获取一个 详细的单位信息 包括单位信息 等级信息 能力信息 用于显示在前端
     *
     * @param type
     * @param level
     * @return
     */
    @Override
    @Cacheable(CatchKey.UNIT_INFO)
    public UnitInfo getUnitInfo(Integer id, Integer level) {
        UnitMes unitMes = getById(id);
        UnitLevelMes unitLevelMesMes = unitLevelMesService.getUnitLevelMes(id, level);
        List<Ability> abilityList = abilityService.getUnitAbilityList(Integer.valueOf(id));
        return new UnitInfo(unitMes, unitLevelMesMes, abilityList);
    }

    /**
     * 获取当前模板单位可以股买的
     *
     * @param hasLoad 领主是否还存活 存活就无法购买
     * @return
     */
    @Override
    @Cacheable(CatchKey.TEMPLATE_CAN_BUY_UNITS)
    public List<UnitMes> getCanBuyUnit(Integer templateId) {
        List<UnitMes> list = unitMesDao.selectCanTradeUnit(templateId);
        return list;
    }


    @Override
    @Cacheable("maxCheapUnit")
    public UnitMes getMaxCheapUnit() {
        return null;
    }

    @Override
    public List<UnitMes> getUserEnableUnitList(Integer userId) {
        QueryWrapper<UnitMes> wrapper = new QueryWrapper<>();
        wrapper.eq("create_user_id", userId);
        wrapper.eq("enable", CommonConstant.YES);
        return unitMesDao.selectList(wrapper);
    }

    @Override
    public List<UnitMes> getDefaultUnitList() {
        return getUserEnableUnitList(CommonConstant.ADMIN_ID);
    }

    @Override
    public void updateInfoById(UnitMes baseInfo) {
        unitMesDao.updateInfoById(baseInfo);
    }
}
