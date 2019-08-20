package com.mihao.ancient_empire.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.util.*;

public class StringUtil {
    private static Logger logger = LoggerFactory.getLogger(StringUtil.class);

    public final static String parseMD5(String s) {
        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str).toLowerCase();
        } catch (Exception e) {
            logger.error("generate md5 error, {}", s, e);
            return null;
        }
    }

    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    // 将[]分解成ListString
    public static List<String> preseStringList(String string) {
        string = string.substring(1, string.length()-1);
        String[] strings = string.split(",");
        return Arrays.asList(strings);
    }

    // 将[]分解成ListString
    public static List<String> preseStringListAddMes(String string, String mes) {
        string = string.substring(1, string.length()-1);
        String[] strings = string.split(",");
        for (int i = 0; i < strings.length; i++) {
            strings[i] = mes + strings[i];
        }
        return Arrays.asList(strings);
    }

    public static int[] parseInts (String string) {
        string.substring(1, string.length()-1);
        String[] strings = string.split(",");
        int[] ints = new int[string.length()];
        for (int i = 0; i < strings.length; i++) {
            ints[i] = Integer.valueOf(strings[i]);
        }
        return ints;
    }


    public static boolean isContentValue (String[] strings, String s) {
        boolean isContent = false;
        for (String string : strings) {
            if (string.equals(s)) {
                isContent = true;
                return isContent;
            }
        }
        return isContent;
    }

    public static String parseString(String[] strings) {
        StringBuffer stringBuffer = new StringBuffer("[");
        for (int i = 0; i < strings.length; i++) {
            stringBuffer.append(strings[i]);
            if (i < strings.length-1) {
                stringBuffer.append(",");
            }
        }
        stringBuffer.append("]");
        return stringBuffer.toString();
    }

    public static List<String> parseList (String[] strings) {
        if (strings.length < 1) {
            return null;
        }
        List list = new ArrayList();
        for (int i = 0; i < strings.length; i++) {
            list.add(strings[i]);
        }
        return list;
    }

    public static String[] parseStrings (List<String> stringList) {
        String[] strings = new String[stringList.size()];
        for (int i = 0; i < stringList.size(); i++) {
            strings[i] = stringList.get(i);
        }
        return strings;
    }


    /**
     * 判断一个字符串不为空
     * @param string
     * @return
     */
    public static boolean isEmpty(String string) {
        if (string != null && !string.equals(""))
            return false;
        return true;
    }
}
