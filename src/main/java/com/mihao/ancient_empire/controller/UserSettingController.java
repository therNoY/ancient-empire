package com.mihao.ancient_empire.controller;


import com.mihao.ancient_empire.common.util.RespHelper;
import com.mihao.ancient_empire.common.vo.RespJson;
import com.mihao.ancient_empire.entity.UserSetting;
import com.mihao.ancient_empire.service.UserSettingService;
import com.mihao.ancient_empire.util.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

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
