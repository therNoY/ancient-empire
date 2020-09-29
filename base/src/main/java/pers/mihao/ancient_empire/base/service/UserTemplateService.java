package pers.mihao.ancient_empire.base.service;

import pers.mihao.ancient_empire.base.entity.UserTemplate;
import com.baomidou.mybatisplus.extension.service.IService;

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

}
