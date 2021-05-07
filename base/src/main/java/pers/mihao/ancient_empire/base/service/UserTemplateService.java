package pers.mihao.ancient_empire.base.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
import pers.mihao.ancient_empire.base.dto.ReqSaveUserTemplateDTO;
import pers.mihao.ancient_empire.base.dto.ReqUserTemplateDTO;
import pers.mihao.ancient_empire.base.dto.TemplateIdDTO;
import pers.mihao.ancient_empire.base.entity.UnitMes;
import pers.mihao.ancient_empire.base.entity.UserTemplate;
import pers.mihao.ancient_empire.base.vo.UserTemplateVO;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author mihao
 * @since 2020-09-22
 */
public interface UserTemplateService extends IService<UserTemplate> {

    /**
     * 通过ID 获取用户模板
     *
     * @param id
     * @return
     */
    UserTemplate getTemplateById(Integer id);

    /**
     * 获取用户模板
     *
     * @param reqUserTemplateDTO
     * @return
     */
    IPage<UserTemplateVO> getUserTemplateWithPage(ReqUserTemplateDTO reqUserTemplateDTO);

    /**
     * 获取用户的模板单位
     *
     * @param templateIdDTO
     * @return
     */
    List<UnitMes> getUserAllTempUnit(TemplateIdDTO templateIdDTO);

    /**
     * 获取用户的草稿模板
     *
     * @param userId
     * @return
     */
    UserTemplateVO getUserDraftTemplate(Integer userId);

    /**
     * 删除用户模板
     *
     * @param userTemplate
     */
    void deleteUserTemplate(UserTemplate userTemplate);



    /**
     * 获取可以下载的模板
     *
     * @param reqUserTemplateDTO
     * @return
     */
    IPage<UserTemplateVO> getDownloadAbleTempWithPage(ReqUserTemplateDTO reqUserTemplateDTO);

    /**
     * 设置模板通用信息
     * @param userTemplates
     */
    void addTemplateExtendInfo(List<UserTemplateVO> userTemplates);
    /**
     * 保存模板信息
     *
     * @param reqSaveUserTemplateDTO
     */
    void saveTemplateInfo(ReqSaveUserTemplateDTO reqSaveUserTemplateDTO);

    /**
     * 根据类型获取最大版本的模板
     *
     * @param templateType
     * @return
     */
    UserTemplate getMaxTemplateByType(String templateType);

    /**
     * 展示模板缓存
     *
     * @param userTemplate
     */
    void delTemplateCatch(UserTemplate userTemplate);

}