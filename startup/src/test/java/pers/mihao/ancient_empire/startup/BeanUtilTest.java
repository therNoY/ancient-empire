package pers.mihao.ancient_empire.startup;

import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.base.mongo.dao.UserRecordRepository;
import pers.mihao.ancient_empire.base.service.UserRecordService;
import pers.mihao.ancient_empire.core.manger.GameContext;

/**
 * @Author mh32736
 * @Date 2021/2/3 18:52
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class BeanUtilTest {

    @Autowired
    UserRecordRepository userRecordRepository;

    @Test
    public void name() {
    }

}
