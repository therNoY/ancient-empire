package pers.mihao.ancient_empire.base.util;

import pers.mihao.ancient_empire.common.config.AppConfig;

import java.io.File;

/**
 * @version 1.0
 * @auther mihao
 * @date 2021\1\5 0005 22:53
 */
public class UploadPathUtil {


    private static final String tempPath = "temp";

    public static String getUploadTemplatePath(String tempId) {
        return AppConfig.get("file.img.upload.path") + File.separator + tempPath + File.separator
                + tempId + File.separator;
    }
}
