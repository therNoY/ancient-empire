package pers.mihao.ancient_empire.common.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author mihao
 * @Date 2021/6/30 20:03
 */
public class ThreadPoolNameUtil {

    private static Map<String, AtomicInteger> map = new HashMap<>(16);

    /**
     * 获取现场的名字
     * @param key
     * @return
     */
    public static String getThreadName(String key) {
        AtomicInteger integer = map.get(key);
        if (integer == null) {
            integer = new AtomicInteger(0);
            map.put(key, integer);
        }
        return key + "-" + integer;
    }

}
