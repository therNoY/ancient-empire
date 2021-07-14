package pers.mihao.ancient_empire.startup.config;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import pers.mihao.ancient_empire.common.annotation.redis.NotGenerator;
import pers.mihao.ancient_empire.common.base_catch.local.LocalCatch;
import pers.mihao.ancient_empire.common.base_catch.local.LocalCatchManger;
import pers.mihao.ancient_empire.common.util.PropertiesUtil;


/**
 * Spring的缓存配置
 *
 * @author mihao
 */
@Configuration
@EnableCaching
public class CatchConfigManger extends CachingConfigurerSupport {

    private static Map<Method, Set<Integer>> notGenerateKeyIndexCatch = new ConcurrentHashMap<>(16);

    /**
     * 设置缓存的命名生成规则 使用缓存名+"::"+"参数.参数..."的形式
     *
     * @return
     */
    @Override
    @Bean
    public KeyGenerator keyGenerator() {
        return new KeyGenerator() {
            @Override
            public Object generate(Object target, Method method, Object... params) {
                Set<Integer> notGenerateKeyIndex = getNoGenerateKey(method);
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < params.length; i++) {
                    if (!notGenerateKeyIndex.contains(i)) {
                        Object obj = params[i];
                        sb.append(obj.toString()).append(".");
                    }
                }
                if (sb.length() > 0) {
                    sb.setLength(sb.length() - 1);
                }
                return sb.toString();
            }
        };
    }

    private Set<Integer> getNoGenerateKey(Method method) {
        Set<Integer> set;
        if ((set = notGenerateKeyIndexCatch.get(method)) == null) {
            set = new HashSet<>();
            Annotation[][] annotations = method.getParameterAnnotations();
            Annotation[] parameterAnnos;
            for (int i = 0; i < annotations.length; i++) {
                parameterAnnos = annotations[i];
                for (Annotation annotation : parameterAnnos) {
                    if (annotation.annotationType().equals(NotGenerator.class)) {
                        set.add(i);
                        break;
                    }
                }
            }
            notGenerateKeyIndexCatch.put(method, set);
        }
        return set;
    }

    /**
     * 配置缓存
     *
     * @param connectionFactory
     * @return
     */
    @Bean
    @ConditionalOnProperty(name = "spring.catch.manger", havingValue = "redis")
    public CacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
        // 设置默认的缓存过期时间
        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration
            .defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(60));

        // 设置自定义的缓存时间
        Map<String, RedisCacheConfiguration> map = new HashMap<>(16);
        // 获取所有自定义缓存时间 时间默认是 m
        Properties properties = PropertiesUtil.getProperties("catch.properties");
        properties.forEach((key, value) -> {
            // 设置用户获取可用 单位的缓存过期时间
            map.put(key.toString(), RedisCacheConfiguration
                .defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(Integer.parseInt(value.toString())))
                .disableCachingNullValues());
        });

        return RedisCacheManager
            .builder(connectionFactory)
            .cacheDefaults(defaultCacheConfig)
            .withInitialCacheConfigurations(map)
            .transactionAware().build();
    }


    @Bean
    @ConditionalOnProperty(name = "spring.catch.manger", havingValue = "local")
    public LocalCatch localCatch(){
        // 设置缓存默认过期1小时
        return new LocalCatch(true, 60 * 60);
    }


    @Bean
    @ConditionalOnProperty(name = "spring.catch.manger", havingValue = "local")
    public CacheManager localCacheManager(LocalCatch localCatch) {
        LocalCatchManger localCatchManger = new LocalCatchManger();
        localCatchManger.setCache(localCatch);
        return localCatchManger;
    }


}