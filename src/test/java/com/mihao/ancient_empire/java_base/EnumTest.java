package com.mihao.ancient_empire.java_base;

import com.mihao.ancient_empire.constant.RedisKey;
import com.mihao.ancient_empire.constant.UnitEnum;
import org.junit.Test;

public class EnumTest {
    @Test
    public void name() {

//        System.out.println(UnitEnum.SOLDIER.getName() + ": " + UnitEnum.SOLDIER.getType());
//        System.out.println(UnitEnum.SOLDIER .equals(UnitEnum.SOLDIER));
//        System.out.println(UnitEnum.WATER_ELEMENT.getName() + ": " + UnitEnum.WATER_ELEMENT.getType());

        System.out.println(UnitEnum.SOLDIER.getName());
        System.out.println(UnitEnum.SOLDIER.getName());

        System.out.println(RedisKey.ENABLE_REGION);
    }
}
