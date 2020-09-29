package pers.mihao.ancient_empire.startup;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import pers.mihao.ancient_empire.base.service.UserMapService;
import pers.mihao.ancient_empire.base.service.UserRecordService;
import pers.mihao.ancient_empire.common.constant.CatchKey;
import pers.mihao.ancient_empire.common.dto.RegisterDto;
import pers.mihao.ancient_empire.common.jdbc.redis.RedisUtil;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTest {

    @Autowired
    RedisUtil redisUtil;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    UserMapService userMapService;
    @Autowired
    UserRecordService recordService;

    @Test
    public void name() {
       Object object = redisUtil.get("f09fbc8d-93d2-4344-b8a6-f3769a644e31");
        RegisterDto registerDto = JSON.parseObject(object.toString(), RegisterDto.class);
       System.out.println(object.toString());
    }


    @Test
    public void name2() {
//       Object o = redisTemplate.opsForValue().get(CatchKey.getKey(CatchKey.USER_RECORD) + "ther2");
//        System.out.println(o);
//       User user = redisHelper.getObject(CatchKey.getKey(CatchKey.USER_RECORD) + "ther2", User.class);
//        System.out.println(user.getName());
        System.out.println(redisUtil.isHaveKey(CatchKey.getKey(CatchKey.USER_RECORD) + "ther2"));
        redisUtil.delKey(CatchKey.getKey(CatchKey.USER_RECORD) + "ther2");
        System.out.println(redisUtil.isHaveKey(CatchKey.getKey(CatchKey.USER_RECORD) + "ther2"));
    }

    @Test
    public void name3() {
        redisUtil.set("222", "vvv");
        System.out.println(redisUtil.get("222"));
        redisUtil.hSet("111", "key1", "value2", 100);
//        redisTemplate.opsForValue().set("222","vvv");
//        System.out.println(redisTemplate.hasKey("222"));
    }


    @Test
    public void name4() {

    }
}
