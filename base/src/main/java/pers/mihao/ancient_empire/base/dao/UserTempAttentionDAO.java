package pers.mihao.ancient_empire.base.dao;

import pers.mihao.ancient_empire.base.dto.ReqUserTemplateDTO;
import pers.mihao.ancient_empire.base.dto.TemplateCountDTO;
import pers.mihao.ancient_empire.base.entity.UserTempAttention;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import pers.mihao.ancient_empire.base.entity.UserTemplate;
import pers.mihao.ancient_empire.common.jdbc.PageModel;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author mihao
 * @since 2020-09-22
 */
public interface UserTempAttentionDAO extends BaseMapper<UserTempAttention> {

    /**
     * 选择模板的引用数和总星星数
     * @param id
     * @return
     */
    TemplateCountDTO selectCountStartByTempId(Integer id);

    /**
     * 获取用户模板
     * @param reqUserTemplateDTO
     * @return
     */
    List<UserTemplate> getAttentionTemplateWithPage(ReqUserTemplateDTO reqUserTemplateDTO);

    /**
     * 获取可以下载的模板
     * @param reqUserTemplateDTO
     * @return
     */
    List<UserTemplate> getDownloadAbleTempWithPage(ReqUserTemplateDTO reqUserTemplateDTO);
}
