package pers.mihao.ancient_empire.common.base_catch.local;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.cache.Cache;
import org.springframework.cache.support.AbstractCacheManager;

/**
 * 本地缓存
 * @Author mihao
 * @Date 2021/7/12 13:47
 */
public class LocalCatchManger extends AbstractCacheManager {

    List<Cache> caches = new ArrayList<>();

    @Override
    protected Collection<? extends Cache> loadCaches() {
        return caches;
    }

    public void setCache(Cache cache){
        caches.add(cache);
    }

    @Override
    public Cache getCache(String name) {
        return new LocalTransactionAwareCacheDecorator(name, caches.get(0));
    }


}
