package pers.mihao.ancient_empire.common.base_catch.redis;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import pers.mihao.ancient_empire.common.base_catch.BaseCatch;

/**
 * 当缓存使用redis时生效
 * @Author mh32736
 * @Date 2021/7/12 11:34
 */
@Component
@ConditionalOnProperty(name = "spring.catch.manger", havingValue = "redis")
public class RedisCatchHolder implements BaseCatch {

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Override
    public long increment(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 设置过期时间
     *
     * @param key
     * @param value
     */
    @Override
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public void setAndExpire(String key, Object value, Long time) {
        set(key, value);
        expire(key, time);
    }

    @Override
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public long delete(String... keys) {
        if (keys.length == 1) {
            redisTemplate.delete(keys[0]);
            return 1;
        } else {
            return redisTemplate.delete(Arrays.asList(keys));
        }
    }

    @Override
    public boolean exist(String key) {
        return redisTemplate.hasKey(key);
    }

    @Override
    public long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }


    @Override
    public boolean expire(String key, Long time) {
        if (time < 0) {
            return false;
        }
        return redisTemplate.expire(key, time, TimeUnit.SECONDS);
    }

    @Override
    public Object hashGet(String key, String item) {
        return redisTemplate.opsForHash().get(key, item);
    }

    @Override
    public Map<Object, Object> hashGetAll(String key) {
        return redisTemplate.opsForHash().entries(key);

    }

    @Override
    public void hashPutAll(String key, Map<String, Object> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    @Override
    public void hashPut(String key, String item, Object value) {
        redisTemplate.opsForHash().put(key, item, value);
    }

    @Override
    public void hashDelete(String key, Object... item) {
        redisTemplate.opsForHash().delete(key, item);
    }

    @Override
    public boolean hashExist(String key, String item) {
        return redisTemplate.opsForHash().hasKey(key, item);
    }


    @Override
    public long setAdd(String key, Object... values) {
        return redisTemplate.opsForSet().add(key, values);
    }


    @Override
    public long getSetSize(String key) {
        return redisTemplate.opsForSet().size(key);
    }


    @Override
    public long setRemove(String key, Object... values) {
        return redisTemplate.opsForSet().remove(key, values);

    }


    @Override
    public List<Object> listRange(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }


    @Override
    public long listSize(String key) {
        return redisTemplate.opsForList().size(key);
    }


    @Override
    public void listAddLast(String key, Object value) {
        redisTemplate.opsForList().rightPush(key, value);
    }

    @Override
    public long listRemoveRange(String key, long count, Object value) {
        return redisTemplate.opsForList().remove(key, count, value);
    }

}
