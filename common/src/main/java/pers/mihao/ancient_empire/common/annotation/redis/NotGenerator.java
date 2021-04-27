package pers.mihao.ancient_empire.common.annotation.redis;

import java.lang.annotation.*;

/**
 * 用户生成缓存的key的时候不参与计算
 * @version 1.0
 * @author mihao
 * @date 2021\1\17 0017 20:42
 */
@Target({ElementType.PARAMETER}) // 只能注解到
@Retention(RetentionPolicy.RUNTIME) // 该注解在运行时生效
@Documented
public @interface NotGenerator {
}
