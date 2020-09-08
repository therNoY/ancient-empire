package pers.mihao.ancient_empire.common.enums;

import java.util.HashMap;
import java.util.Map;

public interface BaseEnum {

    Map<String, String> enumMap = new HashMap();

    /**
     * 单位枚举 获取驼峰命名的type
     * 将枚举改成小写
     * ex WATER_ELEMENT => waterElement
     * @return
     */
    default String type() {
        String key = this.toString();
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
