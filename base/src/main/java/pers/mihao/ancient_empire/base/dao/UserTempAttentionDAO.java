package pers.mihao.ancient_empire.base.dao;

import pers.mihao.ancient_empire.base.dto.ReqUserTemplateDTO;
import pers.mihao.ancient_empire.base.dto.CountSumDTO;
import pers.mihao.ancient_empire.base.entity.UserTempAttention;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import pers.mihao.ancient_empire.base.vo.UserTemplateVO;

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
     * @param type
     * @return
     */
    CountSumDTO selectCountStartByTempType(String type);

    /**
     * 获取用户下载模板
     * @param reqUserTemplateDTO
     * @return
     */
    List<UserTemplateVO> getAttentionTemplateWithPage(ReqUserTemplateDTO reqUserTemplateDTO);

    /**
     * 获取可以下载的模板
     * @param reqUserTemplateDTO
     * @return
     */
    List<UserTemplateVO> getDownloadAbleTempWithPage(ReqUserTemplateDTO reqUserTemplateDTO);



}
