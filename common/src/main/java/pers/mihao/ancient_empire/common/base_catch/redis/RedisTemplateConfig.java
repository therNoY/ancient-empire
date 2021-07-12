package pers.mihao.ancient_empire.common.base_catch.redis;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * 条件注入 当配置文件中有 spring.catch.manger=redis注入该配置
 * @Author mihao
 * @Date 2021/7/12 11:27
 */
@Configuration
public class RedisTemplateConfig {

    /**
     * 自定义 redis 的序列化规则
     *
     * @param factory
     * @return
     */
    @Bean
    @ConditionalOnProperty(name = "spring.catch.manger", havingValue = "redis")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
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
