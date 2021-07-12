package pers.mihao.ancient_empire.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.mihao.ancient_empire.auth.service.UserService;
import pers.mihao.ancient_empire.auth.util.LoginUserHolder;
import pers.mihao.ancient_empire.base.bo.UnitInfo;
import pers.mihao.ancient_empire.base.constant.BaseConstant;
import pers.mihao.ancient_empire.base.constant.VersionConstant;
import pers.mihao.ancient_empire.base.controller.GameFileController;
import pers.mihao.ancient_empire.base.dao.UnitMesDAO;
import pers.mihao.ancient_empire.base.dto.ApiOrderDTO;
import pers.mihao.ancient_empire.base.dto.ReqSaveUnitMesDTO;
import pers.mihao.ancient_empire.base.entity.Ability;
import pers.mihao.ancient_empire.base.entity.UnitLevelMes;
import pers.mihao.ancient_empire.base.entity.UnitMes;
import pers.mihao.ancient_empire.base.service.AbilityService;
import pers.mihao.ancient_empire.base.service.UnitAbilityService;
import pers.mihao.ancient_empire.base.service.UnitLevelMesService;
import pers.mihao.ancient_empire.base.service.UnitMesService;
import pers.mihao.ancient_empire.base.util.IPageHelper;
import pers.mihao.ancient_empire.base.vo.UnitMesVO;
import pers.mihao.ancient_empire.common.constant.CacheKey;
import pers.mihao.ancient_empire.common.constant.CommonConstant;
import pers.mihao.ancient_empire.common.dto.ApiConditionDTO;
import pers.mihao.ancient_empire.common.util.BeanUtil;
import pers.mihao.ancient_empire.common.util.StringUtil;
import pers.mihao.ancient_empire.common.vo.AeException;

/**
 * <p>
 * 单位信息表 服务实现类
 * </p>
 *
 * @author mihao
 * @since 2019-08-11
 */
@Service
public class UnitMesServiceImpl extends ServiceImpl<UnitMesDAO, UnitMes> implements UnitMesService {

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserService userService;
    @Autowired
    UnitMesDAO unitMesDao;
    @Autowired
    UnitLevelMesService unitLevelMesService;
    @Autowired
    AbilityService abilityService;
    @Autowired
    UnitMesService unitMesService;
    @Autowired
    UnitAbilityService unitAbilityService;
    @Autowired
    GameFileController gameFileController;


    /**
     * 获取所有单位信息
     *
     * @param page
     * @return
     */
    @Override
    public IPage<UnitMes> selectUnitMesByCreateUserWithPage(ApiConditionDTO apiConditionDTO) {
        List<UnitMes> unitMes = unitMesDao.selectUnitMesByCreateUserWithPage(apiConditionDTO);
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
        if (StringUtil.isBlack(unitMes.getAttackType())){
            unitMes.setAttackType(BaseConstant.PHYSICAL);
        }
        if (unitMes.getTradable() == null) {
            unitMes.setTradable(1);
        }
        if (unitMes.getId() != null) {
            unitMes.setCreateUserId(LoginUserHolder.getUserId());
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
                throw new AeException(500);
            }

        }
    }

    /**
     * 根据创建者的用户获取 可用的Unit 获取所有可用单位信息
     *
     * @return
     */
    @Override
    @Cacheable(CacheKey.ENABLE_UNIT)
    public List<UnitMes> getEnableUnitByTempId(String tempId) {
        log.info(">>>>用户从数据库中获取可用单位<<<<");
        List<UnitMes> unitMes = unitMesDao.getEnableUnitByTempId(tempId);
        return unitMes;
    }


    @Override
    @Cacheable(CacheKey.UNIT_MES)
    public UnitMes getUnitMesById(Integer id) {
        return getById(id);
    }

    /**
     * 获取一个 详细的单位信息 包括单位信息 等级信息 能力信息 用于显示在前端
     *
     * @param type
     * @param level
     * @return
     */
    @Override
    @Cacheable(CacheKey.UNIT_INFO)
    public UnitInfo getUnitInfo(Integer id, Integer level) {
        UnitMes unitMes = getById(id);
        UnitLevelMes unitLevelMesMes = unitLevelMesService.getUnitLevelMes(id, level);
        List<Ability> abilityList = abilityService.getUnitAbilityList(Integer.valueOf(id));
        return new UnitInfo(unitMes, unitLevelMesMes, abilityList);
    }

    /**
     * 获取当前模板单位可以购买的单位
     *
     * @param templateId 领主是否还存活 存活就无法购买
     * @return
     */
    @Override
    @Cacheable(CacheKey.TEMPLATE_CAN_BUY_UNITS)
    public List<UnitMes> getCanBuyUnit(Integer templateId) {
        List<UnitMes> list = unitMesDao.selectCanTradeUnit(templateId);
        return list;
    }


    @Override
    public List<UnitMes> getUserEnableUnitList(Integer userId) {
        QueryWrapper<UnitMes> wrapper = new QueryWrapper<>();
        wrapper.eq("create_user_id", userId);
        wrapper.eq("enable", CommonConstant.YES);
        wrapper.eq("status", VersionConstant.OFFICIAL);
        List<UnitMes> unitMesList = unitMesDao.selectList(wrapper).stream()
            .sorted(Comparator.comparingInt(UnitMes::getVersion)).collect(Collectors.toList());

        Map<String, UnitMes> unitVersionMap = new HashMap<>(16);
        for (UnitMes unitMes : unitMesList) {
            unitVersionMap.put(unitMes.getType(), unitMes);
        }

        List<UnitMes> res = new ArrayList<>();
        for (Map.Entry<String, UnitMes> entry : unitVersionMap.entrySet()) {
            res.add(entry.getValue());
        }
        return res;
    }

    @Override
    public List<UnitMes> getBaseUnitList() {
        QueryWrapper<UnitMes> wrapper = new QueryWrapper<>();
        wrapper.eq("create_user_id", CommonConstant.ADMIN_ID);
        wrapper.eq("enable", CommonConstant.YES);
        wrapper.lt("id", 13);
        return unitMesDao.selectList(wrapper);
    }

    @Override
    @Transactional
    public void updateInfoById(UnitMes baseInfo) {
        unitMesDao.updateById(baseInfo);
    }

    @Override
    public UnitMes getDraftVersionUnitMes(String type) {
        QueryWrapper<UnitMes> wrapper = new QueryWrapper<>();
        wrapper.eq("type", type);
        wrapper.eq("status", VersionConstant.DRAFT);
        return unitMesDao.selectOne(wrapper);
    }

    @Override
    public IPage<UnitMesVO> getUserDownloadUnitMesWithPage(ApiConditionDTO conditionDTO) {
        List<UnitMesVO> unitMesList = unitMesDao.getUserDownloadUnitMesWithPage(conditionDTO);
        UnitMes maxVersionUnit;
        for (UnitMesVO unitMes : unitMesList) {
            maxVersionUnit = unitMesService.getMaxVersionUnitByType(unitMes.getType());
            if (maxVersionUnit != null && maxVersionUnit.getVersion() > unitMes.getVersion()) {
                unitMes.setMaxVersion(maxVersionUnit.getVersion());
            }
        }
        return IPageHelper.toPage(unitMesList, conditionDTO);
    }

    @Override
    public IPage<UnitMesVO> getDownloadAbleUnitMesWithPage(ApiOrderDTO orderDTO) {
        List<UnitMesVO> unitMesList = unitMesDao.getDownloadAbleUnitMesWithPage(orderDTO);
        UnitMes unitMes;
        for (UnitMesVO vo : unitMesList) {
            unitMes = unitMesService.getMaxVersionUnitByType(vo.getType());
            BeanUtil.copyValueByGetSet(unitMes, vo);
            unitMes.setCreateUserId(Math.abs(unitMes.getCreateUserId()));
            vo.setCreateUserName(userService.getUserById(unitMes.getCreateUserId()).getName());
        }
        return IPageHelper.toPage(unitMesList, orderDTO);
    }

    @Override
    public void updateUnitStatusByType(String type, Integer status) {
        QueryWrapper<UnitMes> wrapper = new QueryWrapper<>();
        wrapper.eq("type", type);
        List<UnitMes> unitMesList = unitMesDao.selectList(wrapper);
        for (UnitMes unitMes : unitMesList) {
            unitMes.setEnable(status);
            if (status.equals(VersionConstant.DELETE)) {
                // 修改创建用户为 -id 便于找到
                unitMes.setCreateUserId(unitMes.getCreateUserId() * -1);
            }
            unitMes.setUpdateTime(LocalDateTime.now());
            unitMesDao.updateById(unitMes);
        }
    }

    @Override
    @Cacheable(CacheKey.UNIT_MAX_VERSION)
    public UnitMes getMaxVersionUnitByType(String type) {
        Integer version = unitMesDao.getMaxVersionByType(type);
        QueryWrapper<UnitMes> wrapper = new QueryWrapper<>();
        wrapper.eq("version", version).eq("type", type);
        return unitMesDao.selectOne(wrapper);
    }

    @Override
    @CacheEvict(CacheKey.UNIT_MAX_VERSION)
    public void delMaxVersionCatch(String type) {
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveUnitInfo(ReqSaveUnitMesDTO reqSaveUnitMesDTO) {
        Integer unitId = reqSaveUnitMesDTO.getBaseInfo().getId();

        if (VersionConstant.DRAFT.equals(reqSaveUnitMesDTO.getOptType())) {
            if (reqSaveUnitMesDTO.getBaseInfo().getStatus().equals(VersionConstant.OFFICIAL)) {
                // 当前是正式版本 新加一个版本作为草稿
                UnitMes unitMes = BeanUtil.deptClone(reqSaveUnitMesDTO.getBaseInfo());
                unitMes.setStatus(VersionConstant.DRAFT);
                unitMes.setVersion(reqSaveUnitMesDTO.getBaseInfo().getVersion() + 1);
                unitMes.setId(null);
                unitMes.setCreateTime(LocalDateTime.now());
                unitMes.setUpdateTime(LocalDateTime.now());
                saveUnitMes(unitMes);
                unitId = unitMes.getId();
            } else {
                // 当前也是草稿版本 直接更新
                reqSaveUnitMesDTO.getBaseInfo().setUpdateTime(LocalDateTime.now());
                updateInfoById(reqSaveUnitMesDTO.getBaseInfo());
            }
        } else if (VersionConstant.OFFICIAL.equals(reqSaveUnitMesDTO.getOptType())) {
            if (reqSaveUnitMesDTO.getBaseInfo().getStatus().equals(VersionConstant.OFFICIAL)) {
                // 当前是正式版本 新加一个版本作为草稿
                UnitMes unitMes = BeanUtil.deptClone(reqSaveUnitMesDTO.getBaseInfo());
                unitMes.setStatus(VersionConstant.OFFICIAL);
                unitMes.setVersion(reqSaveUnitMesDTO.getBaseInfo().getVersion() + 1);
                unitMes.setId(null);
                unitMes.setCreateTime(LocalDateTime.now());
                unitMes.setUpdateTime(LocalDateTime.now());
                saveUnitMes(unitMes);
                unitId = unitMes.getId();
            } else {
                // 当前是草稿版本 直接更新
                reqSaveUnitMesDTO.getBaseInfo().setUpdateTime(LocalDateTime.now());
                reqSaveUnitMesDTO.getBaseInfo().setStatus(VersionConstant.OFFICIAL);
                updateInfoById(reqSaveUnitMesDTO.getBaseInfo());
            }
            unitMesService.delMaxVersionCatch(reqSaveUnitMesDTO.getBaseInfo().getType());
        } else {
            // 新增单位  首先保存单位
            UnitMes unitMes = BeanUtil.deptClone(reqSaveUnitMesDTO.getBaseInfo());
            unitMes.setStatus(VersionConstant.OFFICIAL);
            unitMes.setVersion(0);
            unitMes.setId(null);
            unitMes.setType(CommonConstant.UNDER_LINE);
            unitMes.setCreateUserId(reqSaveUnitMesDTO.getUserId());
            // TODO 这里设置漠然启用
            unitMes.setEnable(BaseConstant.YES);
            unitMes.setCreateTime(LocalDateTime.now());
            unitMes.setUpdateTime(LocalDateTime.now());
            saveUnitMes(unitMes);

            unitMes.setType(BaseConstant.UNIT_TYPE + unitMes.getId());
            unitMes.setImgIndex(unitMes.getId().toString());
            updateInfoById(unitMes);
            unitId = unitMes.getId();

            // 生成相应的文件
            try {
                gameFileController.moveUnitImgName2Id(reqSaveUnitMesDTO.getNewUploadImg(), unitMes.getId());
            } catch (IOException e) {
                log.error("单位图片复制错误", e);
                throw new AeException(e.getMessage());
            }
        }

        // 2.更新能力信息
        unitAbilityService.updateUnitAbility(unitId, reqSaveUnitMesDTO.getAbilityInfo());
        // 3.更新等级信息
        for (UnitLevelMes levelMes : reqSaveUnitMesDTO.getLevelInfoData()) {
            levelMes.setUnitId(unitId);
            unitLevelMesService.saveUnitLevelMesList(levelMes);
        }

    }
}
