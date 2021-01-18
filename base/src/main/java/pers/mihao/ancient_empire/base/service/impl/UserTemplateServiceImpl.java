package pers.mihao.ancient_empire.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import pers.mihao.ancient_empire.base.dao.UserTempAttentionDAO;
import pers.mihao.ancient_empire.base.dao.UserTemplateDAO;
import pers.mihao.ancient_empire.base.dto.ReqUserTemplateDTO;
import pers.mihao.ancient_empire.base.dto.TemplateCountDTO;
import pers.mihao.ancient_empire.base.dto.TemplateIdDTO;
import pers.mihao.ancient_empire.base.entity.UnitMes;
import pers.mihao.ancient_empire.base.entity.UserTemplate;
import pers.mihao.ancient_empire.base.service.UserTemplateService;
import pers.mihao.ancient_empire.base.util.IPageHelper;
import pers.mihao.ancient_empire.common.constant.CatchKey;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author mihao
 * @since 2020-09-22
 */
@Service
public class UserTemplateServiceImpl extends ServiceImpl<UserTemplateDAO, UserTemplate> implements UserTemplateService {

    @Autowired
    UserTemplateDAO userTemplateDAO;

    @Autowired
    UserTempAttentionDAO userTempAttentionDAO;

    @Cacheable(CatchKey.USER_TEMP)
    @Override
    public UserTemplate selectById(String id) {
        return getById(id);
    }


    @Override
    public IPage<UserTemplate> getUserTemplateWithPage(ReqUserTemplateDTO reqUserTemplateDTO) {
        Page<UserTemplate> userTemplatePage = new Page<>(reqUserTemplateDTO.getPageStart(), reqUserTemplateDTO.getPageSize());
        QueryWrapper<UserTemplate> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", reqUserTemplateDTO.getUserId());
        wrapper.eq("status", 1);
        IPage<UserTemplate> templateList = userTemplateDAO.selectPage(userTemplatePage, wrapper);
        addTempInfo(templateList.getRecords());
        return templateList;
    }

    @Override
    public List<UnitMes> getUserAllTempUnit(TemplateIdDTO templateIdDTO) {
        return userTemplateDAO.getUserAllTempUnit(templateIdDTO);
    }

    @Override
    public UserTemplate getUserDraftTemplate(Integer userId) {
        QueryWrapper<UserTemplate> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.eq("status", 0);
        UserTemplate template = userTemplateDAO.selectOne(wrapper);
        return template;
    }

    @Override
    public void deleteUserTemplate(Integer userId, String id) {
        userTemplateDAO.updateUserTemplateStatusById(userId, id, -1);
    }

    @Override
    public IPage<UserTemplate> getAttentionTemplateWithPage(ReqUserTemplateDTO reqUserTemplateDTO) {
        List<UserTemplate> userTemplates = userTempAttentionDAO.getAttentionTemplateWithPage(reqUserTemplateDTO);
        addTempInfo(userTemplates);
        return IPageHelper.toPage(userTemplates, reqUserTemplateDTO);
    }

    private void addTempInfo(List<UserTemplate> userTemplates) {
        TemplateIdDTO templateIdDTO;
        TemplateCountDTO templateCountDTO;
        for (UserTemplate template : userTemplates) {
            templateCountDTO = userTempAttentionDAO.selectCountStartByTempId(template.getId());
            template.setCountStart(templateCountDTO.getSum() == null ? 0 : templateCountDTO.getSum()/templateCountDTO.getCount());
            template.setLinkNum(templateCountDTO.getCount());
            templateIdDTO = new TemplateIdDTO();
            templateIdDTO.setTemplateId(template.getId().toString());
            template.setBindUintList(getUserAllTempUnit(templateIdDTO));
        }
    }

    @Override
    public IPage<UserTemplate> getDownloadAbleTempWithPage(ReqUserTemplateDTO reqUserTemplateDTO) {
        List<UserTemplate> userTemplates =  userTempAttentionDAO.getDownloadAbleTempWithPage(reqUserTemplateDTO);
        addTempInfo(userTemplates);
        return IPageHelper.toPage(userTemplates, reqUserTemplateDTO);
    }
}
