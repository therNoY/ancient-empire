package pers.mihao.ancient_empire.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesUtil {

    private static  Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);
    /**
     * 根据 配置文件返回错误码
     * @param property
     * @return
     */
    public static String getProperties (String path, String property) {
        InputStream is = PropertiesUtil.class.getClassLoader().getResourceAsStream(path);
        BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("utf-8")));
        Properties properties = new Properties();
        try {
            properties.load(br);
            return properties.getProperty(property);
        } catch (IOException e) {
            logger.error("", e);
        }
        return null;
    }

    /**
     * 根据 配置文件返回错误码
     * @return
     */
    public static Properties getProperties (String path) {
        InputStream is = PropertiesUtil.class.getClassLoader().getResourceAsStream(path);
        BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("utf-8")));
        Properties properties = new Properties();
        try {
            properties.load(br);
            return properties;
        } catch (IOException e) {
            logger.error("", e);
        }finally {
            try {
                br.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


}
