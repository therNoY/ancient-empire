package pers.mihao.ancient_empire.core.dao;

import java.util.List;
import pers.mihao.ancient_empire.base.entity.UserMap;
import pers.mihao.ancient_empire.common.dto.ApiConditionDTO;
import pers.mihao.ancient_empire.core.entity.UserMapAttention;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author mihao
 * @since 2021-04-26
 */
public interface UserMapAttentionDao extends BaseMapper<UserMapAttention> {

    /**
     * 分页查询用户下载地图
     * @param apiConditionDTO
     * @return
     */
    List<UserMap> getUserDownloadMapWithPage(ApiConditionDTO apiConditionDTO);
}
