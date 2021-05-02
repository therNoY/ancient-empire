package pers.mihao.ancient_empire.base.service;

import pers.mihao.ancient_empire.base.entity.UserTempAttention;
import com.baomidou.mybatisplus.extension.service.IService;
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

}
