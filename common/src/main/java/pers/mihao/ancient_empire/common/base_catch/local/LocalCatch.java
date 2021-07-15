package pers.mihao.ancient_empire.common.base_catch.local;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.cache.support.AbstractValueAdaptingCache;
import org.springframework.cache.support.SimpleValueWrapper;
import pers.mihao.ancient_empire.common.base_catch.BaseCatch;
import pers.mihao.ancient_empire.common.base_catch.DataSerializable;
import pers.mihao.ancient_empire.common.base_catch.DataSerializableByJson;

/**
 * 实现java本地缓存能力
 *
 * @Author mh32736
 * @Date 2021/7/12 11:35
 */
public class LocalCatch extends AbstractValueAdaptingCache implements BaseCatch {

    boolean allowNullValues;
    Map<String, ExpireValue<Object>> stringMap;
    Map<String, ExpireValue<Map<String, ExpireValue<Object>>>> hashMap;
    Map<String, ExpireValue<Set<ExpireValue<Object>>>> setMap;
    Map<String, ExpireValue<List<ExpireValue<Object>>>> listMap;
    DataSerializable dataSerializable;
    long defaultTtlTime;

    public LocalCatch(boolean allowNullValues, long defaultTtlTime) {
        super(allowNullValues);
        this.defaultTtlTime = defaultTtlTime;
        this.allowNullValues = allowNullValues;
        this.stringMap = new ConcurrentHashMap<>(16);
        this.hashMap = new ConcurrentHashMap<>(16);
        this.setMap = new ConcurrentHashMap<>(16);
        this.listMap = new ConcurrentHashMap<>(16);
        this.dataSerializable = new DataSerializableByJson();
    }

    @Override
    protected Object lookup(Object o) {
        return get(o.toString());
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public Object getNativeCache() {
        return this;
    }

    @Override
    public <T> T get(Object key, Callable<T> callable) {
        ValueWrapper result = super.get(key);
        if (result != null) {
            return (T) result.get();
        } else {
            T value = valueFromLoader(key, callable);
            set(key.toString(), value);
            return value;
        }
    }

    private <T> T valueFromLoader(Object key, Callable<T> valueLoader) {
        try {
            return valueLoader.call();
        } catch (Exception var3) {
            throw new ValueRetrievalException(key, valueLoader, var3);
        }
    }

    @Override
    public void put(Object key, Object value) {
        set(key.toString(), value);
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        if (!exist(key.toString())) {
            set(key.toString(), value);
            return new SimpleValueWrapper(value);
        } else {
            return null;
        }
    }

    @Override
    public void evict(Object o) {
        delete(o.toString());
    }

    @Override
    public void clear() {
        stringMap.clear();
    }


    @Override
    public long increment(String key, long delta) {
        Object object = get(key);
        if (!(object instanceof Long)) {
            throw new RuntimeException("自增key对应的value不是一个合法的值" + object);
        }
        Long longV = (Long) object;
        longV += delta;
        put(key, delta);
        return longV;
    }

    @Override
    public void set(String key, Object value) {
        stringMap.put(key, new ExpireValue<>(value));
    }

    @Override
    public void setAndExpire(String key, Object value, Long time) {
        stringMap.put(key, new ExpireValue<>(value, time));
    }

    @Override
    public boolean expire(String key, Long time) {
        ExpireValue expireValue = stringMap.get(key);
        if (expireValue == null) {
            return false;
        }
        synchronized (expireValue) {
            expireValue.expire = System.currentTimeMillis() + time;
        }
        return true;
    }

    @Override
    public Object get(String key) {
        ExpireValue<Object> value = stringMap.get(key);
        if (value != null && value.isExpire()) {
            stringMap.remove(key);
            return null;
        }
        return value == null ? null : value.getValue();
    }

    @Override
    public long delete(String... keys) {
        long success = 0;
        for (String key : keys) {
            success += (stringMap.remove(key) == null ? 0 : 1);
        }
        return success;
    }

    @Override
    public boolean exist(String key) {
        return stringMap.get(key) != null;
    }

    @Override
    public long getExpire(String key) {
        ExpireValue expireValue = stringMap.get(key);
        return expireValue == null ? 0 : expireValue.expire;
    }

    @Override
    public Object hashGet(String key, String item) {
        return null;
    }

    @Override
    public Map<Object, Object> hashGetAll(String key) {
        return null;
    }

    @Override
    public void hashPutAll(String key, Map<String, Object> map) {

    }

    @Override
    public void hashPut(String key, String item, Object value) {

    }

    @Override
    public void hashDelete(String key, Object[] item) {

    }

    @Override
    public boolean hashExist(String key, String item) {
        return false;
    }

    @Override
    public long setAdd(String key, Object[] values) {
        return 0;
    }

    @Override
    public long getSetSize(String key) {
        return 0;
    }

    @Override
    public long setRemove(String key, Object[] values) {
        return 0;
    }

    @Override
    public List<Object> listRange(String key, long start, long end) {
        return null;
    }

    @Override
    public long listSize(String key) {
        return 0;
    }

    @Override
    public void listAddLast(String key, Object value) {

    }

    @Override
    public long listRemoveRange(String key, long count, Object value) {
        return 0;
    }

    class ExpireValue<T> implements Serializable {

        private Object value;
        /**
         * 过期时间-1永不过期
         */
        Long expire;

        Class tClass;

        boolean isCollection;

        public ExpireValue() {
        }

        public ExpireValue(T value) {
            this(value, defaultTtlTime);
        }

        public ExpireValue(T value, Long expire) {
            this.value = value == null ? null : dataSerializable.serialObj(value);
            this.isCollection = (value instanceof Collection);
            if (isCollection) {
                Collection collection = (Collection) value;
                if (collection.size() > 0) {
                    this.tClass = collection.iterator().next().getClass();
                } else {
                    this.tClass = Object.class;
                }
            } else {
                this.tClass = value == null ? null : value.getClass();
            }
            this.expire = System.currentTimeMillis() + expire * 1000;
        }

        boolean isExpire() {
            return this.expire != -1L && System.currentTimeMillis() > expire;
        }

        public T getValue() {
            return value == null ? null : (T) dataSerializable.unSerialObj(this.value, this.tClass, isCollection);
        }
    }
}
