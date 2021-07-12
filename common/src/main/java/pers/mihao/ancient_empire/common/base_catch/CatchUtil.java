package pers.mihao.ancient_empire.common.base_catch;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import pers.mihao.ancient_empire.common.util.ApplicationContextHolder;
import pers.mihao.ancient_empire.common.util.StringUtil;

/**
 * @Author mihao
 * @Date 2021/7/12 11:27
 */
public class CatchUtil {

    static BaseCatch baseCatch;

    static {
        baseCatch = ApplicationContextHolder.getBean(BaseCatch.class);
    }

    /**
     * 递减
     *
     * @param key
     * @param delta
     * @return
     */
    public static long decrease(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        return baseCatch.increment(key, -delta);
    }

    /**
     * 递增
     *
     * @param key
     * @param delta
     * @return
     */
    public static long increment(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return baseCatch.increment(key, delta);
    }

    public static void set(String key, Object value) {
        baseCatch.set(key, value);
    }

    public static void set(String key, Object value, Long time) {
        baseCatch.setAndExpire(key, value, time);
    }

    public static void setJsonString(String key, Object value, Long time) {
        baseCatch.setAndExpire(key, JSONObject.toJSONString(value), time);
    }

    /**
     * 获取单个值
     *
     * @param key
     * @return
     */
    public static Object get(String key) {
        return baseCatch.get(key);
    }

    /**
     * 获取单个值
     *
     * @param key
     * @return
     */
    public static <T> T getObject(String key, Class<T> clazz) {
        ObjectMapper ob = new ObjectMapper();
        T t = null;
        try {
            String json = ob.writeValueAsString(baseCatch.get(key));
            t = ob.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException("获取错误");
        }
        return t;
    }

    /**
     * 获取单个值
     *
     * @param key
     * @return
     */
    public static <T> T getObjectFromJson(String key, Class<T> clazz) {
        String jsonString = (String) baseCatch.get(key);
        if (StringUtil.isNotBlack(jsonString)) {
            return JSONObject.parseObject(jsonString, clazz);
        }
        return null;
    }

    // =====================base===================

    /**
     * 删除keys
     *
     * @param keys
     * @return
     */
    public static long delKey(String... keys) {
        return baseCatch.delete(keys);
    }

    /**
     * 是否存在
     *
     * @param key
     * @return
     */
    public static boolean isHaveKey(String key) {
        return baseCatch.exist(key);
    }

    /**
     * 获取过期时间 木偶人返回s
     *
     * @param key
     * @return
     */
    public static long getExpire(String key) {
        return baseCatch.getExpire(key);
    }

    /**
     * 设置过期时间 默认 秒
     *
     * @return
     */
    public static boolean expire(String key, Long time) {
        if (time < 0) {
            return false;
        }
        return baseCatch.expire(key, time);
    }

    /**
     * HashGet
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return 值 161
     */
    public static Object hGet(String key, String item) {
        return baseCatch.hashGet(key, item);
    }

    /**
     * HashGet
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return 值 161
     */
    public static <T> T hGet(String key, String item, Class<T> clazz) {
        return (T) baseCatch.hashGet(key, item);
    }

    /**
     * 获取hashKey对应的所有键值
     *
     * @param key 键
     * @return 对应的多个键值
     */
    public static Map<Object, Object> hmGet(String key) {
        return baseCatch.hashGetAll(key);

    }

    /**
     * HashSet
     *
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    public static boolean hmSet(String key, Map<String, Object> map) {
        try {
            baseCatch.hashPutAll(key, map);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * HashSet 并设置时间
     *
     * @param key  键
     * @param map  对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败 197
     */
    public static boolean hmSet(String key, Map<String, Object> map, long time) {
        try {
            baseCatch.hashPutAll(key, map);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @return true 成功 false失败
     */
    public static boolean hSet(String key, String item, Object value) {
        try {
            baseCatch.hashPut(key, item, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @param time  时间(秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */

    public static boolean hSet(String key, String item, Object value, long time) {
        try {
            baseCatch.hashPut(key, item, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除hash表中的值
     *
     * @param key  键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    public static void hDel(String key, Object... item) {
        baseCatch.hashDelete(key, item);
    }

    /**
     * 判断hash表中是否有该项的值
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    public static boolean hHasKey(String key, String item) {
        return baseCatch.hashExist(key, item);
    }

    /**
     * 将数据放入set缓存
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public static long sSet(String key, Object... values) {
        try {
            return baseCatch.setAdd(key, values);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

    }

    /**
     * 将set数据放入缓存
     *
     * @param key    键
     * @param time   时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public static long sSet(String key, long time, Object... values) {
        try {
            Long count = baseCatch.setAdd(key, values);
            if (time > 0) {
                expire(key, time);
            }
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取set缓存的长度
     *
     * @param key 键
     * @return 358
     */
    public static long sGetSetSize(String key) {
        try {
            return baseCatch.getSetSize(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 移除值为value的
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    public static long setRemove(String key, Object... values) {
        try {
            Long count = baseCatch.setRemove(key, values);
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

    }

    // ===============================list=================================

    /**
     * 获取list缓存的内容
     *
     * @param key   键
     * @param start 开始
     * @param end   结束 0 到 -1代表所有值
     * @return
     */
    public static List<Object> lGet(String key, long start, long end) {
        try {
            return baseCatch.listRange(key, start, end);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取list缓存的长度
     *
     * @param key 键
     * @return 405
     */

    public static long lGetListSize(String key) {
        try {
            return baseCatch.listSize(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }


    /**
     * 通过索引 获取list中的值
     *
     * @param key   键
     * @param index 索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return 420
     */
    public static Object lGetIndex(String key, long index) {
        try {
            return baseCatch.listRange(key, index, 0).get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值 time  时间(秒)
     * @return 436
     */

    public static boolean lSet(String key, Object value) {
        try {
            baseCatch.listAddLast(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    public static boolean lSet(String key, Object value, long time) {
        try {
            baseCatch.listAddLast(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值 time  时间(秒)
     * @return 472
     */
    public static boolean lSet(String key, List<Object> value) {
        try {
            baseCatch.listAddLast(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return 490
     */

    public static boolean lSet(String key, List<Object> value, long time) {
        try {
            baseCatch.listAddLast(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 移除N个值为value
     *
     * @param key   键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    public static long lRemove(String key, long count, Object value) {
        try {
            Long remove = baseCatch.listRemoveRange(key, count, value);
            return remove;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
