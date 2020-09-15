package pers.mihao.ancient_empire.startup.java_base;

import org.junit.Test;
import pers.mihao.ancient_empire.base.enums.UnitEnum;
import pers.mihao.ancient_empire.common.util.EnumUtil;

public class EnumTest {
    @Test
    public void name() {

//        System.out.println(UnitEnum.SOLDIER.getName() + ": " + UnitEnum.SOLDIER.type());
//        System.out.println(UnitEnum.SOLDIER .equals(UnitEnum.SOLDIER));
//        System.out.println(UnitEnum.WATER_ELEMENT.getName() + ": " + UnitEnum.WATER_ELEMENT.type());
//
//        System.out.println(UnitEnum.SOLDIER.getName());
//        System.out.println(UnitEnum.SOLDIER.getName());
//
//        System.out.println(RedisKey.ENABLE_REGION);
    }

    @Test
    public void name2() {
        UnitEnum type = EnumUtil.valueOf(UnitEnum.class, "waterElement");

        System.out.println(type.type());
    }

    @Test
    public void switchNullTest(){
        String s = null;
        switch (s){
            case "1":
                System.out.println("1");
                break;
        }
    }


}
