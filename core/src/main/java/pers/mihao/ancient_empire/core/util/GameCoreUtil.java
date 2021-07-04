package pers.mihao.ancient_empire.core.util;

import java.util.Map;
import pers.mihao.ancient_empire.common.enums.LanguageEnum;
import pers.mihao.ancient_empire.common.util.PropertiesUtil;

/**
 * @Author mihao
 * @Date 2021/6/13 20:30
 */
public class GameCoreUtil {

    static Map<String, Map<String, String>> gameCoreMap;

    static {
        gameCoreMap = PropertiesUtil.getLanguagePropertiesMap("gameCore-*.properties");
    }

    /**
     * 获取国际话参数
     *
     * @param proper
     * @param lang
     * @param args
     * @return
     */
    public static String getMessageByLang(Globalization globalization, LanguageEnum language) {
        String value = gameCoreMap.get(language.type()).get(globalization.proper);
        return PropertiesUtil.replaceArgs(value, globalization.args);
    }

    /**
     * 国际化
     */
    public static Globalization getMessage(String proper, Object... args) {
        return new Globalization(proper, args);
    }

    public static class Globalization {

        private String proper;

        private Object[] args;

        public Globalization(String proper, Object[] args) {
            this.proper = proper;
            this.args = args;
        }

        public String getProper() {
            return proper;
        }

        public void setProper(String proper) {
            this.proper = proper;
        }

        public Object[] getArgs() {
            return args;
        }

        public void setArgs(Object[] args) {
            this.args = args;
        }
    }

}
