package pers.mihao.ancient_empire.core.listener;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pers.mihao.ancient_empire.common.util.PropertiesUtil;
import pers.mihao.ancient_empire.core.listener.chapter.AbstractChapterListener;

/**
 * @Author mh32736
 * @Date 2021/4/1 17:37
 */
public class ChapterUtil {

    static Logger log = LoggerFactory.getLogger(ChapterUtil .class);
    static Map<String, String> map;

    static {
        map = new HashMap<>();
        Properties properties = PropertiesUtil.getProperties("chapter-zh.properties");
        properties.forEach((key, value)->{
            map.put(key.toString(), value.toString());
        });
    }


    public static AbstractChapterListener getChapterClass(String mapName) {
        String chapterClass = map.get(String.valueOf(mapName));
        AbstractChapterListener listener = null;
        try {
            listener = (AbstractChapterListener) Class.forName(chapterClass).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return listener;
    }

    public static String getMessage(String key) {
        String message = map.get(key);
        return message;
    }

}
