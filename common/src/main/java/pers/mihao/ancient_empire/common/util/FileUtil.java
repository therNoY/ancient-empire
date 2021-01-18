package pers.mihao.ancient_empire.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @version 1.0
 * @auther mihao
 * @date 2021\1\10 0010 16:56
 */
public class FileUtil {

    static Logger log = LoggerFactory.getLogger(FileUtil.class);

    /**
     * 创建文件
     *
     * @param filePath 文件路径
     * @param bytes    文件流
     */
    public static void createFile(String filePath, byte[] bytes) throws IOException {
        Path path = Paths.get(filePath);
        Path parentPath = path.getParent();
        if (!Files.exists(parentPath)) {
            Files.createDirectory(parentPath);

        }
        Files.write(path, bytes);
    }
}
