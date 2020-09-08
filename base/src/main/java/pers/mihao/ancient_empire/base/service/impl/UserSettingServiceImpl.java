package pers.mihao.ancient_empire.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import pers.mihao.ancient_empire.auth.util.AuthUtil;
import pers.mihao.ancient_empire.base.dao.RegionMesDao;
import pers.mihao.ancient_empire.base.dao.UserSettingDao;
import pers.mihao.ancient_empire.base.entity.UserSetting;
import pers.mihao.ancient_empire.base.service.UserSettingService;
import pers.mihao.ancient_empire.common.constant.RedisKey;
import pers.mihao.ancient_empire.common.util.RedisHelper;


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
