package pers.mihao.ae;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import pers.mihao.ancient_empire.base.entity.RegionMes;
import pers.mihao.ancient_empire.base.service.RegionMesService;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * @version 1.0
 * @author mihao
 * @date 2020\9\24 0024 20:10
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class Test {

    @Autowired
    RegionMesService regionMesService;

    @org.junit.Test
    public void name() {
        String filePath = "C:\\Users\\Administrator\\Desktop\\Region";
        File file = new File(filePath);
        List<RegionMes> list = regionMesService.list();
        for (RegionMes rs : list) {
            List<File> files = Arrays.asList(file.listFiles());
            for (File f : files) {
                if ((f.getName().substring(0, f.getName().lastIndexOf("\\."))).equals(rs.getType())) {
                      break;
                }
            }

        }
    }
}
