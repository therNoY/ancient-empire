package pers.mihao.ancient_empire.common.util.reflact;

import java.util.UUID;
import org.junit.Test;
import pers.mihao.ancient_empire.common.annotation.NotGenerator;

import java.util.List;

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

    public void testMethod(@NotGenerator String id){

    }
}
