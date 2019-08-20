package com.mihao.ancient_empire.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

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
}
