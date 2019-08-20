package com.mihao.ancient_empire.java_base;

import org.junit.Test;

public class IntegerTest {

    @Test
    public void name() {
        Integer a = new Integer(-2);
        Integer b = new Integer(-2);
        System.out.println(a == b);
        System.out.println(a.equals(b));
    }
}
