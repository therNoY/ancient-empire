package pers.mihao.ancient_empire.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import pers.mihao.ancient_empire.base.dto.ReqUserTemplateDTO;
import pers.mihao.ancient_empire.base.dto.TemplateIdDTO;
import pers.mihao.ancient_empire.base.entity.UserTempAttention;
import pers.mihao.ancient_empire.base.dao.UserTempAttentionDAO;
import pers.mihao.ancient_empire.base.entity.UserTemplate;
import pers.mihao.ancient_empire.base.service.UserTempAttentionService;
import org.springframework.stereotype.Service;
import pers.mihao.ancient_empire.base.service.UserTemplateService;
import pers.mihao.ancient_empire.base.util.IPageHelper;
import pers.mihao.ancient_empire.base.vo.UserTemplateVO;
import pers.mihao.ancient_empire.common.mybatis_plus_helper.ComplexKeyServiceImpl;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author mihao
 * @since 2020-09-22
 */
@Service
public class UserTempAttentionServiceImpl extends ComplexKeyServiceImpl<UserTempAttentionDAO, UserTempAttention> implements UserTempAttentionService {

    @Autowired
    UserTempAttentionDAO userTempAttentionDAO;

    @Autowired
    UserTemplateService userTemplateService;

    @Override
    public void removeUserAttention(Integer userId, String id) {
        QueryWrapper<UserTempAttention> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.eq("template_id", id);
        this.remove(wrapper);
    }

    @Override
    public IPage<UserTemplateVO> getAttentionTemplateWithPage(ReqUserTemplateDTO reqUserTemplateDTO) {
        List<UserTemplateVO> userTemplates = userTempAttentionDAO.getAttentionTemplateWithPage(reqUserTemplateDTO);
        UserTemplate template;
        for (UserTemplateVO templateVO : userTemplates) {
            template = userTemplateService.getMaxTemplateByType(templateVO.getTemplateType());
            templateVO.setMaxVersion(template.getVersion());
        }
        userTemplateService.addTemplateExtendInfo(userTemplates);
        return IPageHelper.toPage(userTemplates, reqUserTemplateDTO);
    }

    @Override
    public void updateMaxVersion(TemplateIdDTO id) {
        UserTemplate userTemplate = userTemplateService.getTemplateById(id.getTemplateId());
        if (userTemplate != null) {
            UserTemplate newVersion = userTemplateService.getMaxTemplateByType(userTemplate.getTemplateType());
            QueryWrapper<UserTempAttention> wrapper = new QueryWrapper<>();
            wrapper.eq("user_id", id.getUserId());
            wrapper.eq("template_id", id.getTemplateId());
            UserTempAttention attention = userTempAttentionDAO.selectOne(wrapper);
            remove(wrapper);

            attention.setTemplateId(newVersion.getId());
            saveOrUpdate(attention);
        }


    }
}
