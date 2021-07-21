package pers.mihao.ancient_empire.common.base_catch.local;

import java.util.concurrent.Callable;
import org.springframework.cache.Cache;
import org.springframework.cache.transaction.TransactionAwareCacheDecorator;
import pers.mihao.ancient_empire.common.constant.CacheKey;

/**
 * 执行缓存
 * @Author mihao
 * @Date 2021/7/12 22:19
 */
public class LocalTransactionAwareCacheDecorator extends TransactionAwareCacheDecorator {

    private String name;

    public LocalTransactionAwareCacheDecorator(String name, Cache targetCache) {
        super(targetCache);
        this.name = CacheKey.getKey(name);
    }

    @Override
    public void evict(Object key) {
        super.evict(this.name + key);
    }

    @Override
    public ValueWrapper get(Object key) {
        return super.get(this.name + key);
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        return super.get(this.name + key, type);
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        return super.get(this.name + key, valueLoader);
    }

    @Override
    public void put(Object key, Object value) {
        super.put(this.name + key, value);
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        return super.putIfAbsent(this.name + key, value);
    }
}
