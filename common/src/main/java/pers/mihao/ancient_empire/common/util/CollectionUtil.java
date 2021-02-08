package pers.mihao.ancient_empire.common.util;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CollectionUtil {
    private static Logger log = LoggerFactory.getLogger(CollectionUtil.class);

    public static boolean isNotEmpty(Collection collection) {
        if (collection != null && collection.size() > 0) {
            return true;
        }
        return false;
    }

    public static boolean isEmpty(Collection collection) {
        if (collection != null && collection.size() > 0) {
            return false;
        }
        return true;
    }

    /**
     * 从集合中获取一个随机
     * @param collection
     * @param <T>
     * @return
     */
    public static <T> T getRandom(List<T> collection) {
        if (collection != null && collection.size() > 0) {
            return collection.get(IntegerUtil.getRandomIn(collection.size() - 1));
        }
        return null;
    }

    /**
     * 合并集合
     * @param listArray
     * @param <T>
     * @return
     */
    public static <T> List<T> assign(List<T>... listArray) {
        List<T> result = new ArrayList<>();
        for (List<T> list : listArray) {
            result.addAll(list);
        }
        return result;
    }

    /**
     * 合并集合去重
     * @param listArray
     * @param <T>
     * @return
     */
    public static <T> List<T> assignDistinct(List<T>... listArray) {
        return assign(listArray).stream().distinct().collect(Collectors.toList());
    }

    /**
     * 合并集合去重指定比较方法
     * @param compare
     * @param listArray
     * @param <T>
     * @return
     */
    public static <T,V> List<T> assignDistinct(Function<T, V> compare, List<T>... listArray) {
        Set<Object> set = new HashSet<>(16);
        List<T> resList = new ArrayList<>();
        V key;
        for (List<T> list : listArray) {
            for (T obj : list) {
                key = compare.apply(obj);
                if (!set.contains(key)) {
                    resList.add(obj);
                    set.add(key);
                }
            }
        }
        return resList;
    }

    /**
     * 判断是否包含 指定方法
     * @param collection
     * @param compare
     * @param <T>
     * @return
     */
    public static <T> boolean contains(Collection<T> collection, Function<Object, T> compare) {
        return false;
    }
}
