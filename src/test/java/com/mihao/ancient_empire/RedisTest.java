package com.mihao.ancient_empire;

import com.mihao.ancient_empire.common.util.JacksonUtil;
import com.mihao.ancient_empire.common.util.RedisHelper;
import com.mihao.ancient_empire.common.util.RespHelper;
import com.mihao.ancient_empire.common.vo.test_dto.Dog;
import com.mihao.ancient_empire.constant.RedisKey;
import com.mihao.ancient_empire.dto.auth_dto.RegisterDto;
import com.mihao.ancient_empire.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.Field;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTest {

    @Autowired
    RedisHelper redisHelper;
    @Autowired
    RedisTemplate redisTemplate;

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
}
