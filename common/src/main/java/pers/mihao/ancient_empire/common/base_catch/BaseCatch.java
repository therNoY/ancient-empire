package pers.mihao.ancient_empire.common.base_catch;

import java.util.List;
import java.util.Map;

/**
 * 缓存的能力
 * @Author mihao
 * @Date 2021/7/12 11:32
 */
public interface BaseCatch {

    long increment(String key, long delta);

    void set(String key, Object value);

    void setAndExpire(String key, Object value, Long time);

    boolean expire(String key, Long time);

    Object get(String key);

    long delete(String... key);

    boolean exist(String key);

    long getExpire(String key);

    Object hashGet(String key, String item);

    Map<Object, Object> hashGetAll(String key);

    void hashPutAll(String key, Map<String, Object> map);

    void hashPut(String key, String item, Object value);

    void hashDelete(String key, Object[] item);

    boolean hashExist(String key, String item);

    long setAdd(String key, Object[] values);

    long getSetSize(String key);

    long setRemove(String key, Object[] values);

    List<Object> listRange(String key, long start, long end);

    long listSize(String key);

    void listAddLast(String key, Object value);

    long listRemoveRange(String key, long count, Object value);
}
