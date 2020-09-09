package pers.mihao.ancient_empire.startup.java_base;

import org.junit.Test;
import pers.mihao.ancient_empire.common.util.IntegerUtil;

public class RandomTest {

    @Test
    public void name() {
        int min = 60;
        int max = 70;

        for (int i = 0; i < 100; i++) {
            System.out.println((int) (Math.random()*(max - min + 1) + min));
        }
    }

    @Test
    public void name2() {
        for (int i = 0; i < 100; i++) {
            System.out.println(IntegerUtil.getRandomIn(10));
        }

    }
}
