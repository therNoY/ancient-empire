package com.mihao.ancient_empire.common.cache;

import java.util.HashMap;
import java.util.Map;

public class EnumCache {

    private static Map<String, String> enumMap = new HashMap();

    public static String get(String key){
        if (enumMap.get(key) != null) {
            return enumMap.get(key);
        }else {
            String type = key.toLowerCase();
            char[] typeChar = type.toCharArray();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < typeChar.length; i++) {
                char c = typeChar[i];
                if (c != '_') {
                    if (i > 0 && typeChar[i-1] == '_' && sb.length() > 1) {
                        // 只有当他的前一位是"_" 才保持大写
                        sb.append(Character.toUpperCase(c));
                    }else {
                        // 其他情况全部小写
                        sb.append(Character.toLowerCase(c));
                    }
                }
            }
            enumMap.put(key, sb.toString());
            return sb.toString();
        }
    }
}
