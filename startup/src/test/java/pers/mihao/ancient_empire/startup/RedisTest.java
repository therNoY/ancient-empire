package pers.mihao.ancient_empire.startup;

import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import pers.mihao.ancient_empire.base.entity.UserMap;
import pers.mihao.ancient_empire.base.service.UserMapService;
import pers.mihao.ancient_empire.base.service.UserRecordService;
import pers.mihao.ancient_empire.common.constant.RedisKey;
import pers.mihao.ancient_empire.common.dto.RegisterDto;
import pers.mihao.ancient_empire.common.util.JacksonUtil;
import pers.mihao.ancient_empire.common.util.RedisHelper;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTest {

    @Autowired
    RedisHelper redisHelper;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    UserMapService userMapService;
    @Autowired
    UserRecordService recordService;

    @Test
    public void name() {
       Object object = redisHelper.get("f09fbc8d-93d2-4344-b8a6-f3769a644e31");
        RegisterDto registerDto = JacksonUtil.jsonToBean(object.toString(), RegisterDto.class);
       System.out.println(object.toString());
    }


    @Test
    public void name2() {
//       Object o = redisTemplate.opsForValue().get(RedisKey.USER_INFO_ + "ther2");
//        System.out.println(o);
//       User user = redisHelper.getObject(RedisKey.USER_INFO_ + "ther2", User.class);
//        System.out.println(user.getName());
        System.out.println(redisHelper.isHaveKey(RedisKey.USER_INFO_ + "ther2"));
        redisHelper.delKey(RedisKey.USER_INFO_ + "ther2");
        System.out.println(redisHelper.isHaveKey(RedisKey.USER_INFO_ + "ther2"));
    }

    @Test
    public void name3() {
        redisHelper.set("222", "vvv");
        System.out.println(redisHelper.get("222"));
        redisHelper.hSet("111", "key1", "value2", 100);
//        redisTemplate.opsForValue().set("222","vvv");
//        System.out.println(redisTemplate.hasKey("222"));
    }


    @Test
    public void name4() {
        List<UserMap> userMaps = userMapService.getEncounterMaps();
        for (UserMap map : userMaps) {
            userMapService.saveMap(map);
        }
    }
}
