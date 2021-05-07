package pers.mihao.ancient_empire.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pers.mihao.ancient_empire.auth.service.UserService;
import pers.mihao.ancient_empire.auth.util.AuthUtil;
import pers.mihao.ancient_empire.base.constant.VersionConstant;
import pers.mihao.ancient_empire.base.dao.UserTempAttentionDAO;
import pers.mihao.ancient_empire.base.dao.UserTemplateDAO;
import pers.mihao.ancient_empire.base.dto.ReqSaveUserTemplateDTO;
import pers.mihao.ancient_empire.base.dto.ReqUserTemplateDTO;
import pers.mihao.ancient_empire.base.dto.TemplateCountDTO;
import pers.mihao.ancient_empire.base.dto.TemplateIdDTO;
import pers.mihao.ancient_empire.base.entity.UnitMes;
import pers.mihao.ancient_empire.base.entity.UnitTemplateRelation;
import pers.mihao.ancient_empire.base.entity.UserTemplate;
import pers.mihao.ancient_empire.base.service.UnitMesService;
import pers.mihao.ancient_empire.base.service.UnitTemplateRelationService;
import pers.mihao.ancient_empire.base.service.UserTemplateService;
import pers.mihao.ancient_empire.base.util.IPageHelper;
import pers.mihao.ancient_empire.base.vo.UserTemplateVO;
import pers.mihao.ancient_empire.common.constant.CatchKey;

import java.util.List;
import pers.mihao.ancient_empire.common.jdbc.redis.RedisUtil;
import pers.mihao.ancient_empire.common.mybatis_plus_helper.ComplexKeyServiceImpl;
import pers.mihao.ancient_empire.common.util.BeanUtil;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author mihao
 * @since 2020-09-22
 */
@Service
public class UserTemplateServiceImpl extends ServiceImpl<UserTemplateDAO, UserTemplate> implements
    UserTemplateService {

    @Autowired
    UserTemplateDAO userTemplateDAO;

    @Autowired
    UserTempAttentionDAO userTempAttentionDAO;

    @Autowired
    UserService userService;

    @Autowired
    UserTemplateService userTemplateService;

    @Autowired
    UnitTemplateRelationService unitTemplateRelationService;

    @Autowired
    UnitMesService unitMesService;

    @Cacheable(CatchKey.USER_TEMP)
    @Override
    public UserTemplate getTemplateById(Integer id) {
        return getById(id);
    }


    @Override
    public IPage<UserTemplateVO> getUserTemplateWithPage(ReqUserTemplateDTO reqUserTemplateDTO) {
        List<UserTemplateVO> userTemplateList = userTemplateDAO.selectUserMaxTemplateWithPage(reqUserTemplateDTO);
        addTemplateExtendInfo(userTemplateList);
        return IPageHelper.toPage(userTemplateList, reqUserTemplateDTO);
    }

    @Override
    public List<UnitMes> getUserAllTempUnit(TemplateIdDTO templateIdDTO) {
        return userTemplateDAO.getUserAllTempUnit(templateIdDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserTemplateVO getUserDraftTemplate(Integer userId) {
        QueryWrapper<UserTemplate> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.eq("status", 0);
        wrapper.eq("version", 0);
        UserTemplate template = userTemplateDAO.selectOne(wrapper);
        UserTemplateVO draftTemp = BeanUtil.copyValueFromParent(template, UserTemplateVO.class);

        if (draftTemp == null) {
            // 为空就创建一个草稿模板
            // 1.获取系统默认模板
            UserTemplate defaultTemp = userTemplateService.getById(1);
            defaultTemp.setId(null);
            draftTemp = BeanUtil.copyValueFromParent(defaultTemp, UserTemplateVO.class);
            draftTemp.setUserId(AuthUtil.getUserId());
            draftTemp.setStatus(VersionConstant.DRAFT);
            draftTemp.setVersion(0);
            draftTemp.setCreateTime(LocalDateTime.now());
            draftTemp.setUpdateTime(LocalDateTime.now());
            userTemplateService.save(draftTemp);
            draftTemp.setTemplateType("TEMPLATE_" + draftTemp.getId());
            userTemplateService.updateById(draftTemp);
            // 2.模板绑定默认单位
            List<UnitMes> defaultUnitMes = unitMesService.getBaseUnitList();
            UnitTemplateRelation relation;
            for (UnitMes unitMes : defaultUnitMes) {
                relation = new UnitTemplateRelation();
                relation.setUnitId(unitMes.getId());
                relation.setTempId(draftTemp.getId());
                unitTemplateRelationService.save(relation);
            }
        }
        return draftTemp;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUserTemplate(UserTemplate userTemplate) {
        if (userTemplate.getStatus().equals(VersionConstant.DRAFT)) {
            removeById(userTemplate);
        }
        QueryWrapper<UserTemplate> wrapper = new QueryWrapper<>();
        wrapper.eq("template_type", userTemplate.getTemplateType());
        List<UserTemplate> historyTemplate = userTemplateDAO.selectList(wrapper);
        for (UserTemplate template : historyTemplate) {
            template.setStatus(VersionConstant.DELETE);
            template.setUpdateTime(LocalDateTime.now());
            template.setUserId(template.getUserId() * -1);
            userTemplateDAO.updateById(template);
        }
        delTemplateCatch(userTemplate);
    }


    @Override
    public IPage<UserTemplateVO> getDownloadAbleTempWithPage(ReqUserTemplateDTO reqUserTemplateDTO) {
        List<UserTemplateVO> userTemplates = userTempAttentionDAO.getDownloadAbleTempWithPage(reqUserTemplateDTO);
        UserTemplate template;
        for (UserTemplateVO templateVO : userTemplates) {
            template = userTemplateService.getMaxTemplateByType(templateVO.getTemplateType());
            BeanUtil.copyValueFromParent(template, templateVO);
        }
        addTemplateExtendInfo(userTemplates);
        return IPageHelper.toPage(userTemplates, reqUserTemplateDTO);
    }


    /**
     * 这只模板通用信息
     * @param userTemplates
     */
    @Override
    public void addTemplateExtendInfo(List<UserTemplateVO> userTemplates) {
        TemplateIdDTO templateIdDTO;
        TemplateCountDTO templateCountDTO;
        for (UserTemplateVO template : userTemplates) {
            templateCountDTO = userTempAttentionDAO.selectCountStartByTempId(template.getId());
            template.setStartCount(
                templateCountDTO.getSum() == null ? 0 : templateCountDTO.getSum());
            template.setDownLoadCount(templateCountDTO.getCount());
            templateIdDTO = new TemplateIdDTO();
            templateIdDTO.setTemplateId(template.getId());
            template.setUserId(Math.abs(template.getUserId()));
            template.setCreateUserName(userService.getUserById(template.getUserId()).getName());
            template.setBindUintList(getUserAllTempUnit(templateIdDTO));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveTemplateInfo(ReqSaveUserTemplateDTO reqSaveUserTemplateDTO) {
        // 1.保存模板信息
        UserTemplate template = (UserTemplate) BeanUtil.copyValueToParent(reqSaveUserTemplateDTO, UserTemplate.class);
        if (template.getStatus().equals(VersionConstant.DRAFT)) {
            // 原本就是草该状态
            template.setStatus(reqSaveUserTemplateDTO.getOptType());
            updateById(template);
        } else if (template.getStatus().equals(VersionConstant.OFFICIAL)) {
            // 原来是正式版本
            template.setVersion(template.getVersion() + 1);
            template.setCreateTime(LocalDateTime.now());
            template.setUpdateTime(LocalDateTime.now());
            template.setId(null);
            template.setStatus(reqSaveUserTemplateDTO.getOptType());
            save(template);
            // 设置新的ID
            reqSaveUserTemplateDTO.setId(template.getId());
        }
        delTemplateCatch(template);
        // 2.保存单位模板关系
        unitTemplateRelationService.saveRelation(reqSaveUserTemplateDTO);
    }

    @Override
    @Cacheable(CatchKey.TEMPLATE_MAX_VERSION)
    public UserTemplate getMaxTemplateByType(String templateType) {
        Integer version  = userTemplateDAO.getMaxVersionByType(templateType);
        QueryWrapper<UserTemplate> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("template_type", templateType)
            .eq("version", version);
        return userTemplateDAO.selectOne(queryWrapper);
    }

    @Override
    public void delTemplateCatch(UserTemplate userTemplate) {
        RedisUtil.delKey(CatchKey.getKey(CatchKey.TEMPLATE_MAX_VERSION) + userTemplate.getTemplateType());
        RedisUtil.delKey(CatchKey.getKey(CatchKey.USER_TEMP) + userTemplate.getId());
    }
}
