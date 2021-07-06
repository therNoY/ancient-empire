package pers.mihao.ancient_empire.core.listener;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.mihao.ancient_empire.auth.util.LoginUserHolder;
import pers.mihao.ancient_empire.common.enums.LanguageEnum;
import pers.mihao.ancient_empire.common.util.PropertiesUtil;
import pers.mihao.ancient_empire.core.listener.chapter.AbstractChapterListener;

/**
 * @Author mihao
 * @Date 2021/4/1 17:37
 */
public class ChapterDialogHelper {

    static Logger log = LoggerFactory.getLogger(ChapterDialogHelper.class);

    static Map<String, Map<String, String>> dialogMap;

    static {
        dialogMap = PropertiesUtil.getLanguagePropertiesMap("chapter-*.properties");
    }


    public static AbstractChapterListener getChapterClass(String mapName) {
        LanguageEnum lang = LoginUserHolder.getLanguage();
        if (lang == null) {
            lang = LanguageEnum.ZH;
        }
        return getChapterClassByLang(mapName, dialogMap.get(lang.type()));
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
        LanguageEnum lang = LoginUserHolder.getLanguage();
        return getMessageByLang(key, dialogMap.get(lang.type()));
    }

    private static String getMessageByLang(String key, Map<String, String> dialogMap) {
        String message = dialogMap.get(key);
        return message;
    }

}
