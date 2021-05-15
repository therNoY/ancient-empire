package pers.mihao.ancient_empire.core.listener;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.mihao.ancient_empire.common.bean.UserLanguageSet;
import pers.mihao.ancient_empire.common.util.PropertiesUtil;
import pers.mihao.ancient_empire.core.listener.chapter.AbstractChapterListener;

/**
 * @Author mh32736
 * @Date 2021/4/1 17:37
 */
public class ChapterDialogHelper {

    static Logger log = LoggerFactory.getLogger(ChapterDialogHelper.class);
    static Map<String, Map<String, String>> dialogMap;

    static {
        dialogMap = PropertiesUtil.getLanguagePropertiesMap("chapter-*.properties");
    }


    public static AbstractChapterListener getChapterClass(String mapName) {
        return getChapterClassByLang(mapName, dialogMap.get(UserLanguageSet.LANG.get().type()));
    }

    private static AbstractChapterListener getChapterClassByLang(String mapName, Map<String, String> dialogMap) {
        String chapterClass = dialogMap.get(String.valueOf(mapName));
        AbstractChapterListener listener = null;
        try {
            listener = (AbstractChapterListener) Class.forName(chapterClass).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return listener;
    }

    public static String getMessage(String key) {
        return getMessageByLang(key, dialogMap.get(UserLanguageSet.LANG.get().type()));
    }

    private static String getMessageByLang(String key, Map<String, String> dialogMap) {
        String message = dialogMap.get(key);
        return message;
    }

}
