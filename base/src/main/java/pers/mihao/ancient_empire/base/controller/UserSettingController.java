package pers.mihao.ancient_empire.base.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pers.mihao.ancient_empire.auth.util.AuthUtil;
import pers.mihao.ancient_empire.base.dto.ReqUserTemplateDTO;
import pers.mihao.ancient_empire.base.entity.UserSetting;
import pers.mihao.ancient_empire.base.entity.UserTemplate;
import pers.mihao.ancient_empire.base.service.UserSettingService;
import pers.mihao.ancient_empire.common.util.RespUtil;
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
        Integer userId = AuthUtil.getUserId();
        userSetting.setUserId(userId);
        userSettingService.updateByUserId(userSetting);
        return RespUtil.successResJson();
    }

}
