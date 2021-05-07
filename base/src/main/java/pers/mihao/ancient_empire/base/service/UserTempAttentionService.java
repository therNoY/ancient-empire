package pers.mihao.ancient_empire.base.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import pers.mihao.ancient_empire.base.dto.ReqUserTemplateDTO;
import pers.mihao.ancient_empire.base.dto.TemplateIdDTO;
import pers.mihao.ancient_empire.base.entity.UserTempAttention;
import com.baomidou.mybatisplus.extension.service.IService;
import pers.mihao.ancient_empire.base.vo.UserTemplateVO;
import pers.mihao.ancient_empire.common.mybatis_plus_helper.ComplexKeyService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author mihao
 * @since 2020-09-22
 */
public interface UserTempAttentionService extends ComplexKeyService<UserTempAttention> {

    /**
     * 删除用户关注的模板
     * @param userId
     * @param id
     */
    void removeUserAttention(Integer userId, String id);


    /**
     * 获取用户关注模板
     *
     * @param reqUserTemplateDTO
     * @return
     */
    IPage<UserTemplateVO> getAttentionTemplateWithPage(ReqUserTemplateDTO reqUserTemplateDTO);

    /**
     * 更新最新版本的模板
     * @param id
     */
    void updateMaxVersion(TemplateIdDTO id);
}
