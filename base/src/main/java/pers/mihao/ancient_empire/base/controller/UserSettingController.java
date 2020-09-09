package pers.mihao.ancient_empire.base.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pers.mihao.ancient_empire.auth.util.AuthUtil;
import pers.mihao.ancient_empire.base.entity.UserSetting;
import pers.mihao.ancient_empire.base.service.UserSettingService;
import pers.mihao.ancient_empire.common.util.RespHelper;
import pers.mihao.ancient_empire.common.vo.RespJson;

/**
 * <p>
 * 用户个性化设置表 前端控制器
 * </p>
 *
 * @author mihao
 * @since 2019-08-13
 */
@RestController
public class UserSettingController {

    @Autowired
    UserSettingService userSettingService;

    @PutMapping("/api/user/useSetting")
    public RespJson saveUserSetting(@RequestBody UserSetting userSetting) {
        Integer userId = AuthUtil.getAuthId();
        userSetting.setUserId(userId);
        userSettingService.updateByUserId(userSetting);
        return RespHelper.successResJson();
    }
}
