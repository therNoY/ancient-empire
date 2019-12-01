package com.mihao.ancient_empire.java_base;

import com.mihao.ancient_empire.common.util.IntegerUtil;
import org.junit.Test;

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
