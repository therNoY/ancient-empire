package pers.mihao.ancient_empire.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import pers.mihao.ancient_empire.auth.util.LoginUserHolder;
import pers.mihao.ancient_empire.base.dao.RegionMesDAO;
import pers.mihao.ancient_empire.base.dao.UserSettingDAO;
import pers.mihao.ancient_empire.base.entity.UserSetting;
import pers.mihao.ancient_empire.base.enums.RegionEnum;
import pers.mihao.ancient_empire.base.service.UserSettingService;
import pers.mihao.ancient_empire.common.constant.CatchKey;
import pers.mihao.ancient_empire.common.jdbc.redis.RedisUtil;
import pers.mihao.ancient_empire.common.util.StringUtil;


/**
 * <p>
 * 用户个性化设置表 服务实现类
 * </p>
 *
 * @author mihao
 * @since 2019-08-13
 */
@Service
public class UserSettingServiceImpl extends ServiceImpl<UserSettingDAO, UserSetting> implements UserSettingService {

    @Autowired
    RegionMesDAO regionMesDao;
    @Autowired
    UserSettingDAO userSettingDao;

    @Override
    @Cacheable(CatchKey.USER_SETTING)
    public UserSetting getUserSettingById(Integer id) {
        UserSetting userSetting = userSettingDao.selectById(id);
        if (StringUtil.isBlack(userSetting.getMapInitRegionType())) {
            userSetting.setMapInitRegionType(RegionEnum.SEA.type());
        }
        return userSetting;
    }

    @Override
    public void updateByUserId(UserSetting userSetting) {
        // 删除缓存
        RedisUtil.delKey(CatchKey.getKey(CatchKey.USER_SETTING) + LoginUserHolder.getUserId());
        QueryWrapper<UserSetting> wrapper = new QueryWrapper();
        wrapper.eq("user_id", userSetting.getUserId());
        userSettingDao.update(userSetting, wrapper);
    }
}
