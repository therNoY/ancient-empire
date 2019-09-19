package com.mihao.ancient_empire.java_base;

import org.junit.Test;

import java.io.File;

public class StringTest {

    @Test
    public void newBreak() {
        System.out.println(File.separator);
    }

    @Test
    public void name2() {
        String url = "http://10.0.11.154:8080/recorderfileserver/resources/2019-09-03/webchat_file/f570d7745f374899b05460f0b719b022.jpg";
        System.out.println(url.substring(0, url.lastIndexOf("/") + 1));
    }

    @Test
    public void name3() {
        System.out.println("2".toLowerCase());
    }
}
