package pers.mihao.ancient_empire.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import pers.mihao.ancient_empire.base.entity.UserSetting;

/**
 * <p>
 * 用户个性化设置表 服务类
 * </p>
 *
 * @author mihao
 * @since 2019-08-13
 */
public interface UserSettingService extends IService<UserSetting> {

    /**
     * 获取用户个性化设置
     * @param id
     * @return
     */
    UserSetting getUserSettingById(Integer id);

    void updateByUserId(UserSetting userSetting);
}
