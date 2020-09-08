package pers.mihao.ancient_empire.common.util;

public class EnumUtil {

    /**
     * 将String 转成存在的枚举类型
     * @param enumType
     * @param type
     * @param <T>
     * @return
     */
    public static <T extends Enum<T>> T valueOf(Class<T> enumType, String type) {
        // 1.将type 转成大写
        char[] types = type.toCharArray();
        StringBuffer sb = new StringBuffer();
        for (char c : types) {
            if ('a' <= c && c <= 'z') {
                sb.append(Character.toUpperCase(c));
            }else if ('A' <= c && c <= 'Z' ){
                sb.append("_").append(c);
            }else {
                sb.append(c);
            }
        }
        return Enum.valueOf(enumType, sb.toString());
    }
}
