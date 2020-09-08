package pers.mihao.ancient_empire.common.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.mihao.ancient_empire.common.util.PropertiesUtil;

/**
 * 该文件从错误码的位置文件中获取 错误码信息 只初始化一次
 */
public class ErrorCode {

    static Logger log = LoggerFactory.getLogger(ErrorCode.class);
    static Map<String, String> map;

    static {
        map = new HashMap<>();
        Properties properties = PropertiesUtil.getProperties("err.properties");
        properties.forEach((key, value)->{
            map.put(key.toString(), value.toString());
        });
    }


    public static String getErrorMes(Integer errorCode){
        String errMes = map.get(String.valueOf(errorCode));
        if (errMes == null) {
            log.error("没有注册的错误码 {}", errorCode);
            return "error";
        }else {
            return errMes;
        }
    }
}
