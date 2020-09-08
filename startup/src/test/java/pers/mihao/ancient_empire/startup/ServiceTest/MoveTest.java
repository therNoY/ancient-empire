package pers.mihao.ancient_empire.startup.ServiceTest;

import pers.mihao.ancient_empire.core.websocket.service.WsMoveAreaService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MoveTest {

    @Autowired
    WsMoveAreaService moveAreaService;

    @Test
    public void getMoveArea() {
        String recordId = "a4652f73943347c1b7329ac67e5e1850"; // 初始化地图
//        moveAreaService.getMoveArea(recordId, new ReqUnitIndexDto(0, 0));
    }
}
