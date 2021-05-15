package pers.mihao.ancient_empire.common.config;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.mihao.ancient_empire.common.bean.UserLanguageSet;
import pers.mihao.ancient_empire.common.util.PropertiesUtil;

/**
 * 该文件从错误码的位置文件中获取 错误码信息 只初始化一次
 *
 * @author hspcadmin
 */
public class ErrorCodeHelper {

    static Logger log = LoggerFactory.getLogger(ErrorCodeHelper.class);
    static Map<String, Map<String, String>> errCodeMap;

    static {
        errCodeMap = PropertiesUtil.getLanguagePropertiesMap("err-*.properties");
    }


    public static String getErrorMes(Integer errorCode) {
        return getByLanguageMap(errorCode, errCodeMap.get(UserLanguageSet.LANG.get().type()));
    }

    private static String getByLanguageMap(Integer errorCode, Map<String, String> errCodeMap) {
        String errMes = errCodeMap.get(String.valueOf(errorCode));
        if (errMes == null) {
            log.error("没有注册的错误码 {}", errorCode);
            return "error";
        } else {
            return errMes;
        }
    }
}
