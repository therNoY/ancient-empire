package pers.mihao.ancient_empire.base.service;

import pers.mihao.ancient_empire.base.dto.UserMapIdDTO;
import pers.mihao.ancient_empire.base.entity.UserMapAttention;
import pers.mihao.ancient_empire.common.mybatis_plus_helper.ComplexKeyService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author mihao
 * @since 2021-04-26
 */
public interface UserMapAttentionService extends ComplexKeyService<UserMapAttention> {

    /**
     * 更新版本
     * @param id
     */
    void updateMaxVersion(UserMapIdDTO id);
}
