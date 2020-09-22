package pers.mihao.ancient_empire.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.mihao.ancient_empire.auth.util.AuthUtil;
import pers.mihao.ancient_empire.base.bo.UnitInfo;
import pers.mihao.ancient_empire.base.dao.UnitMesDao;
import pers.mihao.ancient_empire.base.entity.Ability;
import pers.mihao.ancient_empire.base.entity.UnitLevelMes;
import pers.mihao.ancient_empire.base.entity.UnitMes;
import pers.mihao.ancient_empire.base.enums.UnitEnum;
import pers.mihao.ancient_empire.base.service.AbilityService;
import pers.mihao.ancient_empire.base.service.UnitLevelMesService;
import pers.mihao.ancient_empire.base.service.UnitMesService;
import pers.mihao.ancient_empire.common.constant.RedisKey;
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
public class UnitMesServiceImpl extends ServiceImpl<UnitMesDao, UnitMes> implements UnitMesService {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UnitMesDao unitMesDao;
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
    public IPage<UnitMes> getList(Page<UnitMes> page) {
        return unitMesDao.selectPage(page, null);
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
            unitMes.setCreateUserId(AuthUtil.getAuthId());
            unitMesDao.updateById(unitMes);
        } else {
            unitMesDao.insert(unitMes);

            if (unitMes.getId() != null) {
                for (int i = 0; i < 4; i++) {
                    UnitLevelMes unitLevelMes = new UnitLevelMes();
                    unitLevelMes.setUnitId(unitMes.getId());
                    unitLevelMes.setLevel(i);
                    unitLevelMesService.insert(unitLevelMes);
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
    @Cacheable(value = RedisKey.ENABLE_UNIT)
    public List<UnitMes> getEnableUnitByUserId(Integer id) {
        log.info(">>>>用户从数据库中获取可用单位<<<<");
        QueryWrapper wrapper = new QueryWrapper<UnitMes>();
        wrapper.eq("enable", 1);
//        wrapper.eq("create_user_id", id);
        List<UnitMes> unitMes = unitMesDao.selectList(wrapper);
        return unitMes;
    }

    /**
     * 获取单位信息byTyoe
     *
     * @param type
     * @return
     */
    @Cacheable(RedisKey.UNIT_MES)
    @Override
    public UnitMes getByType(String type) {
        UnitMes unitMes = unitMesDao.selectOne(new QueryWrapper<UnitMes>().eq("type", type));
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
    @Cacheable(RedisKey.UNIT_INFO)
    public UnitInfo getUnitInfo(String type, Integer level) {
        UnitMes unitMes = getByType(type);
        UnitLevelMes unitLevelMesMes = unitLevelMesService.getUnitLevelMes(type, level);
        List<Ability> abilityList = abilityService.getUnitAbilityListByType(type);
        return new UnitInfo(unitMes, unitLevelMesMes, abilityList);
    }

    /**
     * 获取可购买的所有棋子信息
     * @param hasLoad 领主是否还存活 存活就无法购买
     * @return
     */
    @Override
    @Cacheable(RedisKey.UNIT_INFO_LIST)
    public List<UnitInfo> getUnitInfoList(boolean hasLoad) {
        List<UnitInfo> unitInfoList = new ArrayList<>();
        // 1. 获取数据库中所有单位信息
        QueryWrapper<UnitMes> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("tradeable", true);
        queryWrapper.eq("enable", true);
        List<UnitMes> unitMesList = unitMesDao.selectList(queryWrapper);

        for (UnitMes unitMes : unitMesList) {
            // 不是lord 直接添加
            if (!unitMes.getType().equals(UnitEnum.LORD.type())) {
                UnitInfo unitInfo = new UnitInfo();
                UnitLevelMes unitLevelMesMes = unitLevelMesService.getUnitLevelMes(unitMes.getType(), 1);
                List<Ability> abilityList = abilityService.getUnitAbilityListByType(unitMes.getType());
                unitInfo.setUnit(unitMes);
                unitInfo.setLevel(unitLevelMesMes);
                unitInfo.setAbilities(abilityList);
                unitInfoList.add(unitInfo);
            }else {
                if (!hasLoad) {
                    // 是lord 而且军队没有 lord 可以添加
                    UnitInfo unitInfo = new UnitInfo();
                    UnitLevelMes unitLevelMesMes = unitLevelMesService.getUnitLevelMes(unitMes.getType(), 1);
                    List<Ability> abilityList = abilityService.getUnitAbilityListByType(unitMes.getType());
                    unitInfo.setUnit(unitMes);
                    unitInfo.setLevel(unitLevelMesMes);
                    unitInfo.setAbilities(abilityList);
                    unitInfoList.add(unitInfo);
                }
            }
        }

        return unitInfoList;
    }


    /**
     * 获取可以购买的单位
     * @return
     */
    @Override
    @Cacheable("enableBuyUnit")
    public List<UnitMes> getEnableBuyUnit(){
        QueryWrapper<UnitMes> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("tradeable", true);
        queryWrapper.eq("enable", true);
        List<UnitMes> unitMesList = unitMesDao.selectList(queryWrapper);
        return unitMesList;
    }

    @Override
    @Cacheable("maxCheapUnit")
    public UnitMes getMaxCheapUnit() {
        List<UnitMes> unitMesList = getEnableBuyUnit();
        int minPrice = Integer.MAX_VALUE;
        UnitMes maxCheapUnit = null;
        for (UnitMes mes : unitMesList) {
            if (mes.getPrice() < minPrice) {
                maxCheapUnit = mes;
                minPrice = mes.getPrice();
            }
        }
        return maxCheapUnit;
    }


}
