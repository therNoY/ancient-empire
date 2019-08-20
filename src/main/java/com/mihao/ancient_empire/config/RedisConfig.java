package com.mihao.ancient_empire.config;

import com.mihao.ancient_empire.common.util.PropertiesUtil;
import com.mihao.ancient_empire.constant.RedisKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport{

    @Bean
    public KeyGenerator keyGenerator() {
        return new KeyGenerator() {
            @Override
            public Object generate(Object target, Method method, Object... params) {
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i< params.length; i++ ) {
                    Object obj = params[i];
                    if (i != params.length - 1) {
                        sb.append(obj.toString()).append(".");
                    }else {
                        sb.append(obj.toString());
                    }
                }
                return sb.toString();
            }
        };
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        /* //设置 全局缓存过期时间
        return RedisCacheManager
                .builder(connectionFactory)
                .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofMinutes(1)))
                .transactionAware()
                .build();*/
        // 设置默认的缓存过期时间
        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration
                .defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(1))
                .disableCachingNullValues();

        // 设置自定义的缓存时间
        Map<String, RedisCacheConfiguration> map = new HashMap();
        // 获取所有自定义缓存时间 时间默认是 m
        Properties properties = PropertiesUtil.getProperties("catch.properties");
        properties.forEach((key, value)->{
            // 设置用户获取可用 单位的缓存过期时间
            map.put(key.toString(), RedisCacheConfiguration
                    .defaultCacheConfig()
                    .entryTtl(Duration.ofMinutes(Integer.valueOf(value.toString())))
                    .disableCachingNullValues());
        });
//        // 设置用户获取可用 单位的缓存过期时间
//        RedisCacheConfiguration enableUnitCache = RedisCacheConfiguration
//                .defaultCacheConfig()
//                .entryTtl(Duration.ofMinutes(2))
//                .disableCachingNullValues();
//        // 设置用户获取可用 单位的缓存过期时间
//        RedisCacheConfiguration userInfo = RedisCacheConfiguration
//                .defaultCacheConfig()
//                .entryTtl(Duration.ofMinutes(10))
//                .disableCachingNullValues();
//        // 设置 遭遇战地图的缓存 过期时间 1小时
//        RedisCacheConfiguration encounterMap = RedisCacheConfiguration
//                .defaultCacheConfig()
//                .entryTtl(Duration.ofHours(1))
//                .disableCachingNullValues();
//
//
//        /* 将的接口放置到缓存中 有效时间是2 m*/
//        map.put(RedisKey.ENABLE_UNIT, enableUnitCache);
//        map.put(RedisKey.USER_INFO, userInfo);
//        map.put(RedisKey.ENCOUNTER_MAP, encounterMap);

        return  RedisCacheManager
                .builder(connectionFactory)
                .cacheDefaults(defaultCacheConfig)
                .withInitialCacheConfigurations(map)
                .transactionAware().build();
    }


    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
        redisTemplate.setConnectionFactory(factory);
        Jackson2JsonRedisSerializer jrs = new Jackson2JsonRedisSerializer(Object.class);
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        //序列化设置 ，这样计算是正常显示的数据，也能正常存储和获取
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setValueSerializer(jrs);
        redisTemplate.setHashKeySerializer(stringSerializer);
        redisTemplate.setHashValueSerializer(jrs);
        return redisTemplate;
    }
}