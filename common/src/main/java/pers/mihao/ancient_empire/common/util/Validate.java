package pers.mihao.ancient_empire.common.util;

public class Validate {

    public static boolean isUrl (String url){
        if (!url.startsWith("https://") && !url.startsWith("http://")) {
            return false;
        }
        return !url.contains("?") && !url.contains("&");
    }
}
