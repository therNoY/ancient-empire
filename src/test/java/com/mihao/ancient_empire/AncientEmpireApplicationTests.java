package com.mihao.ancient_empire;

import com.mihao.ancient_empire.entity.UserSetting;
import com.mihao.ancient_empire.service.UserSettingService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AncientEmpireApplicationTests {

    @Autowired
    UserSettingService userSettingService;

    @Test
    public void contextLoads() {
        UserSetting userSetting = new UserSetting();
        userSetting.setUserId(4);
        userSetting.setMapInitRow(15);
        userSetting.setMapInitColumn(20);
        userSettingService.updateByUserId(userSetting);
    }

}
