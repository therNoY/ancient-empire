package pers.mihao.ancient_empire.startup.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @Author mihao
 * @Date 2021/2/24 9:03
 */
public class ChangeFileNameUtil {

    public static void main(String[] args) throws IOException {
        String filePath = "C:\\Users\\hspcadmin\\Desktop\\新建文件夹";
        File file = new File(filePath);
        File[] files = file.listFiles();
        for (File f : files) {
            if (f.getName().endsWith(".png")) {
                String fileName = f.getName();
                String fileP = f.getParent();
                Files.copy(Paths.get(f.getAbsolutePath()), Paths.get(fileP + File.separator + "red_" + fileName));
                Files.copy(Paths.get(f.getAbsolutePath()), Paths.get(fileP + File.separator + "green_" + fileName));
                Files.copy(Paths.get(f.getAbsolutePath()), Paths.get(fileP + File.separator + "blue_" + fileName));
                Files.copy(Paths.get(f.getAbsolutePath()), Paths.get(fileP + File.separator + "black_" + fileName));
            }
        }
    }

}
