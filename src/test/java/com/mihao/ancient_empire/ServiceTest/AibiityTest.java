package com.mihao.ancient_empire.ServiceTest;

import com.mihao.ancient_empire.constant.AbilityEnum;
import com.mihao.ancient_empire.constant.UnitEnum;
import com.mihao.ancient_empire.entity.Ability;
import com.mihao.ancient_empire.service.AbilityService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AibiityTest {

    @Autowired
    AbilityService abilityService;

    @Test
    public void getMoveArea() {
        List<Ability> abilityList = abilityService.getUnitAbilityListByType(UnitEnum.LORD.type());

        boolean t1 = abilityList.contains(AbilityEnum.VILLAGE_GET.ability());
        boolean t2 = abilityList.contains(new Ability(AbilityEnum.VILLAGE_GET.type()));

        System.out.println(t1 + " " + t2);
    }
}
