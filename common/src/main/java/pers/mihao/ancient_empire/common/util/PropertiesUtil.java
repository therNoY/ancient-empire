package pers.mihao.ancient_empire.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.mihao.ancient_empire.common.constant.CommonConstant;
import pers.mihao.ancient_empire.common.enums.LanguageEnum;

/**
 * 读取文件的工具类
 *
 * @author mihao
 */
public class PropertiesUtil {

    private static Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);

    /**
     * 根据 配置文件返回错误码
     *
     * @param property
     * @return
     */
    public static String getProperties(String path, String property) {
        InputStream is = PropertiesUtil.class.getClassLoader().getResourceAsStream(path);
        assert is != null;
        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        Properties properties = new Properties();
        try {
            properties.load(br);
            return properties.getProperty(property);
        } catch (IOException e) {
            logger.error("", e);
        }
        return null;
    }

    public static Map<String, Map<String, String>> getLanguagePropertiesMap(String path) {
        Map<String, Properties> propertiesMap = getLanguageProperties(path);
        Map<String, Map<String, String>> map = new HashMap<>(propertiesMap.size());
        for (Map.Entry<String, Properties> entry : propertiesMap.entrySet()) {
            Map<String, String> properMap = new HashMap<>(16);
            entry.getValue().forEach((key, value) -> {
                properMap.put(key.toString(), value.toString());
            });
            map.put(entry.getKey(), properMap);
        }

        return map;
    }

    public static Map<String, Properties> getLanguageProperties(String path) {

        if (!path.contains(CommonConstant.START)) {
            throw new RuntimeException("无法获取国际化配置");
        }

        Map<String, Properties> res = new HashMap<>(16);
        Properties properties;
        for (LanguageEnum language : LanguageEnum.values()) {
            path = path.replace(CommonConstant.START, language.type());
            try {
                properties = getProperties(path);
                res.put(language.type(), properties);
            } catch (Exception e) {
                logger.warn("没有对应的国际化配置：{}", path);
            }
        }
        return res;
    }

    /**
     * 读取配置文件
     *
     * @param path
     * @return
     */
    public static Properties getProperties(String path) {
        InputStream is = PropertiesUtil.class.getClassLoader().getResourceAsStream(path);
        assert is != null;
        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        Properties properties = new Properties();
        try {
            properties.load(br);
            return properties;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                br.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
