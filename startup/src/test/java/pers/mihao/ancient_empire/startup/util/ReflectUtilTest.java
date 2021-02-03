package pers.mihao.ancient_empire.startup.util;

import org.junit.Test;
import pers.mihao.ancient_empire.common.util.BeanUtil;
import pers.mihao.ancient_empire.common.vo.test_dto.Dog;

/**
 * @version 1.0
 * @auther mihao
 * @date 2021\2\3 0003 20:27
 */
public class ReflectUtilTest {

    @Test
    public void testSetFieldValue() {
        Dog dog = new Dog(1, "2");

        BeanUtil.deptClone(dog);

    }
}
