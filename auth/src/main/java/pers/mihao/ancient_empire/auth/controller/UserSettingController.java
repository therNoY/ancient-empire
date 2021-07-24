package pers.mihao.ancient_empire.auth.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pers.mihao.ancient_empire.auth.entity.UserSetting;
import pers.mihao.ancient_empire.auth.service.UserSettingService;
import pers.mihao.ancient_empire.auth.util.LoginUserHolder;
import pers.mihao.ancient_empire.common.dto.ApiRequestDTO;

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

    /**
     * 获取用户设置
     * @param requestDTO
     * @return
     */
    @GetMapping("/api/userSetting")
    public UserSetting getUserSetting(ApiRequestDTO requestDTO) {
        return userSettingService.getUserSettingById(requestDTO.getUserId());
    }

    /**
     * 更新用户设置
     * @param userSetting
     */
    @PutMapping("/api/user/useSetting")
    public void saveUserSetting(@RequestBody UserSetting userSetting) {
        Integer userId = LoginUserHolder.getUserId();
        if (userSetting.getUserId().equals(userId)) {
            userSettingService.updateByUserId(userSetting);
        }
    }

}
