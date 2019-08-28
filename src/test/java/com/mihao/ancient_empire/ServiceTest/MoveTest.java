package com.mihao.ancient_empire.ServiceTest;

import com.mihao.ancient_empire.dto.ws_dto.ReqUnitIndexDto;
import com.mihao.ancient_empire.websocket.WebSocketService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MoveTest {

    @Autowired
    WebSocketService webSocketService;

    @Test
    public void getMoveArea() {
        String recordId = "a4652f73943347c1b7329ac67e5e1850"; // 初始化地图
        webSocketService.getMoveArea(recordId, new ReqUnitIndexDto(0, 0));
    }
}
