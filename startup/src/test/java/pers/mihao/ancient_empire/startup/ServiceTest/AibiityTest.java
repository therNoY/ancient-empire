package pers.mihao.ancient_empire.startup.ServiceTest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import pers.mihao.ancient_empire.base.service.AbilityService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AibiityTest {

    @Autowired
    AbilityService abilityService;

    @Test
    public void getMoveArea() {
    }
}
