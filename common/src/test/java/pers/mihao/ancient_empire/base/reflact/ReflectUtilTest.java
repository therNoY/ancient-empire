package pers.mihao.ancient_empire.base.reflact;

import java.util.UUID;
import org.junit.Test;
import pers.mihao.ancient_empire.common.annotation.redis.NotGenerator;
import pers.mihao.ancient_empire.common.dto.ApiConditionDTO;

/**
 * @version 1.0
 * @author mihao
 * @date 2020\9\20 0020 11:16
 */
public class ReflectUtilTest {

    @Test
    public void testGetField(){
        System.out.println(UUID.randomUUID());
    }

    @Test
    public void testMethod(){
        ApiConditionDTO.class.getDeclaredMethods();
    }
}
