package com.mihao.ancient_empire.common.enums;

public interface BaseEnum {


    /**
     * 单位枚举 获取驼峰命名的type
     * @return
     */
    default String getType() {
        String type = this.toString().toLowerCase();
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
        return sb.toString();
    }
}
