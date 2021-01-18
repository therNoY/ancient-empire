package pers.mihao.ancient_empire.base.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import pers.mihao.ancient_empire.base.bo.Unit;
import pers.mihao.ancient_empire.base.dto.ReqUserTemplateDTO;
import pers.mihao.ancient_empire.base.dto.TemplateIdDTO;
import pers.mihao.ancient_empire.base.entity.UnitMes;
import pers.mihao.ancient_empire.base.entity.UserTemplate;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author mihao
 * @since 2020-09-22
 */
public interface UserTemplateService extends IService<UserTemplate> {

    /**
     * 通过ID 获取用户模板
     * @param id
     * @return
     */
    UserTemplate selectById(String id);

    /**
     * 获取用户模板
     * @param reqUserTemplateDTO
     * @return
     */
    IPage<UserTemplate> getUserTemplateWithPage(ReqUserTemplateDTO reqUserTemplateDTO);

    /**
     * 获取用户的模板单位
     * @param templateIdDTO
     * @return
     */
    List<UnitMes> getUserAllTempUnit(TemplateIdDTO templateIdDTO);

    /**
     * 获取用户的草稿模板
     * @param userId
     * @return
     */
    UserTemplate getUserDraftTemplate(Integer userId);

    /**
     * 删除用户模板
     * @param userId
     * @param id
     */
    void deleteUserTemplate(Integer userId, String id);

    /**
     * 获取用户关注模板
     * @param reqUserTemplateDTO
     * @return
     */
    IPage<UserTemplate> getAttentionTemplateWithPage(ReqUserTemplateDTO reqUserTemplateDTO);

    /*
    获取可以下载的模板
     */
    IPage<UserTemplate> getDownloadAbleTempWithPage(ReqUserTemplateDTO reqUserTemplateDTO);
}