package com.mihao.ancient_empire.service;

import com.mihao.ancient_empire.entity.UserSetting;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户个性化设置表 服务类
 * </p>
 *
 * @author mihao
 * @since 2019-08-13
 */
public interface UserSettingService extends IService<UserSetting> {

    UserSetting getUserSettingById(Integer id);

    void updateByUserId(UserSetting userSetting);
}
