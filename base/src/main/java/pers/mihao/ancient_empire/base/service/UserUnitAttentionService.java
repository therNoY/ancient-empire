package pers.mihao.ancient_empire.base.service;

import pers.mihao.ancient_empire.base.entity.UserUnitAttention;
import com.baomidou.mybatisplus.extension.service.IService;
import pers.mihao.ancient_empire.common.mybatis_plus_helper.ComplexKeyService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author mihao
 * @since 2021-04-29
 */
public interface UserUnitAttentionService extends ComplexKeyService<UserUnitAttention> {

    /**
     * 删除用户下载的单位
     * @param userId
     * @param id
     */
    void deleteUserDownloadUnit(Integer userId, Integer id);
}
