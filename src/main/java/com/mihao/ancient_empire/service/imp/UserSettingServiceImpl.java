package com.mihao.ancient_empire.service.imp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mihao.ancient_empire.common.util.RedisHelper;
import com.mihao.ancient_empire.constant.RedisKey;
import com.mihao.ancient_empire.dao.RegionMesDao;
import com.mihao.ancient_empire.dao.UserSettingDao;
import com.mihao.ancient_empire.entity.UserSetting;
import com.mihao.ancient_empire.service.UserSettingService;
import com.mihao.ancient_empire.util.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;


/**
 * <p>
 * 用户个性化设置表 服务实现类
 * </p>
 *
 * @author mihao
 * @since 2019-08-13
 */
@Service
public class UserSettingServiceImpl extends ServiceImpl<UserSettingDao, UserSetting> implements UserSettingService {

    @Autowired
    RegionMesDao regionMesDao;
    @Autowired
    UserSettingDao userSettingDao;
    @Autowired
    RedisHelper redisHelper;

    @Override
    @Cacheable(RedisKey.USER_SETTING)
    public UserSetting getUserSettingById(Integer id) {
        UserSetting userSetting = userSettingDao.selectOne(new QueryWrapper<UserSetting>().eq("user_id", id));
        if (userSetting == null) {
            userSettingDao.insert(new UserSetting(id));
            userSetting = userSettingDao.selectById(id);
        }
        userSetting.setMapInitRegionType(regionMesDao.selectById(userSetting.getMapInitRegionId()).getType());
        return userSetting;
    }

    @Override
    public void updateByUserId(UserSetting userSetting) {
        // 删除缓存
        redisHelper.delKey(RedisKey.USER_SETTING_ + AuthUtil.getAuthId());
        QueryWrapper<UserSetting> wrapper = new QueryWrapper();
        wrapper.eq("user_id", userSetting.getUserId());
        userSettingDao.update(userSetting, wrapper);
    }
}
