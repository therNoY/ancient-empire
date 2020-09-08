package pers.mihao.ancient_empire.startup.java_base;

import pers.mihao.ancient_empire.common.bo.Unit;
import pers.mihao.ancient_empire.common.bo.ws_dto.AttributesPower;
import com.mihao.ancient_empire.util.AppUtil;
import org.junit.Test;

import java.util.Date;

public class IntegerTest {

    @Test
    public void name() {
        Integer a = new Integer(-2);
        Integer b = new Integer(-2);
        System.out.println(a == b);
        System.out.println(a.equals(b));
    }

    @Test
    public void timeTest() throws InterruptedException {
        long start = new Date().getTime();
        Thread.sleep(1000);
        System.out.println(new Date().getTime() - start);
    }

    /**
     * 测试自动装箱和自动拆箱 的效率 计算不要用 Integer 用 int
     * 自动装箱 也会消耗时间
     * 自动拆箱的效率大概是不需要自动拆箱的一般
     */
    @Test
    public void name2() {
        Integer a = new Integer(1);
        int a2 = 1;
        long begin = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            int s = (a + 1);
        }
        long end = System.currentTimeMillis();
        System.out.println("Integer -> int " + (end - begin) + "ms");

        long begin1 = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            Integer s = (a + 1);
        }
        long end1 = System.currentTimeMillis();
        System.out.println("Integer -> int -> Integer " + (end1 - begin1) + "ms");

        long begin2 = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            int s = (a2 + 1);
        }
        long end2 = System.currentTimeMillis();
        System.out.println("int " + (end2 - begin2) + "ms");

        long begin3 = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            Integer s = (a2 + 1);
        }
        long end3 = System.currentTimeMillis();
        System.out.println("int -> Integer " + (end3 - begin3) + "ms");
    }

    @Test
    public void name4() {
        AttributesPower attributesPower = new AttributesPower();
        attributesPower.setNum(30);
        attributesPower.setAddition((float) 1.5);
        System.out.println(attributesPower.attachPower());
    }

    @Test
    public void name5() {
        Unit unit = new Unit();
        unit.setLife(new Integer[]{9, 8});
        System.out.println(AppUtil.getUnitLeft(unit));
    }

    @Test
    public void name6() {
        System.out.println(Integer.valueOf("1"));
        System.out.println(Integer.valueOf('1' + ""));
    }

    @Test
    public void name7() {
        int a1 = 0x40000; // 4 * 16^4 = 4* 4^8 = 4 * 2*16 = 2^2 * 2^16 = 2^18
        int a2 = (int) Math.pow(2, 18);
        System.out.println(a1);
        System.out.println(a2);
    }

    @Test
    public void name8() {
        Integer integer = new Integer(1);
        Integer integer2 = new Integer(1);
        Integer integer3 = Integer.valueOf(1);
        System.out.println(integer == integer2);
        System.out.println(integer == 1);
        System.out.println(integer == integer3);
    }


    @Test
    public void name9() {
        int min = 10;
        int max = 20;

        for (int i = 0; i < 100; i++) {
            System.out.println(Math.round(Math.random() * (max -min)) + min);
        }
    }
}
