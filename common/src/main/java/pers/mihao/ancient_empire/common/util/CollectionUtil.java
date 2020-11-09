package pers.mihao.ancient_empire.common.util;

import java.util.Collection;
import java.util.List;

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
            return collection.get(IntegerUtil.getRandomIn(collection.size()) - 1);
        }
        return null;
    }
}
