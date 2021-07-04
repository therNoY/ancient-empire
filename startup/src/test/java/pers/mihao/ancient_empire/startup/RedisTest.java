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
import pers.mihao.ancient_empire.common.constant.CacheKey;
import pers.mihao.ancient_empire.common.dto.RegisterDTO;
import pers.mihao.ancient_empire.common.jdbc.redis.RedisUtil;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTest {

    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    UserMapService userMapService;
    @Autowired
    UserRecordService recordService;

    @Test
    public void name() {
       Object object = RedisUtil.get("f09fbc8d-93d2-4344-b8a6-f3769a644e31");
        RegisterDTO registerDto = JSON.parseObject(object.toString(), RegisterDTO.class);
       System.out.println(object.toString());
    }


    @Test
    public void name2() {
//       Object o = redisTemplate.opsForValue().get(CatchKey.getKey(CatchKey.USER_RECORD) + "ther2");
//        System.out.println(o);
//       User user = redisHelper.getObject(CatchKey.getKey(CatchKey.USER_RECORD) + "ther2", User.class);
//        System.out.println(user.getName());
        System.out.println(RedisUtil.isHaveKey(CacheKey.getKey(CacheKey.USER_RECORD) + "ther2"));
        RedisUtil.delKey(CacheKey.getKey(CacheKey.USER_RECORD) + "ther2");
        System.out.println(RedisUtil.isHaveKey(CacheKey.getKey(CacheKey.USER_RECORD) + "ther2"));
    }

    @Test
    public void name3() {
        RedisUtil.set("222", "vvv");
        System.out.println(RedisUtil.get("222"));
        RedisUtil.hSet("111", "key1", "value2", 100);
//        redisTemplate.opsForValue().set("222","vvv");
//        System.out.println(redisTemplate.hasKey("222"));
    }


    @Test
    public void name4() {

    }
}
