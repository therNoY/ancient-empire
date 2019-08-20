package com.mihao.ancient_empire.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Properties;

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
