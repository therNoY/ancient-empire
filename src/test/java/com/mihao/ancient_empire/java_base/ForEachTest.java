package com.mihao.ancient_empire.java_base;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ForEachTest {
    @Test
    public void main() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            list.add(String.valueOf(i));
        }
        long begin = System.currentTimeMillis();
        for (Object s : list) {
            s.toString();
        }
        long end = System.currentTimeMillis();
        System.out.println("普通for循环耗时" + (end - begin) + "ms");
        begin = System.currentTimeMillis();
        list.forEach(e -> {
            e.toString();
        });
        end = System.currentTimeMillis();
        System.out.println("普通lambda表达式foreach耗时" + (end - begin) + "ms");
        begin = System.currentTimeMillis();
        list.stream().forEach(e -> {
            e.toString();
        });
        end = System.currentTimeMillis();
        System.out.println("单管道stream耗时" + (end - begin) + "ms");
        begin = System.currentTimeMillis();
        list.parallelStream().forEach(e -> {
            e.toString();
        });
        end = System.currentTimeMillis();
        System.out.println("多管道stream耗时" + (end - begin) + "ms");
    }

    @Test
    public void main2() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            list.add(String.valueOf(i));
        }
        long begin = System.currentTimeMillis();
        for (Object s : list) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            s.toString();
        }
        long end = System.currentTimeMillis();
        System.out.println("普通for循环耗时" + (end - begin) + "ms");

        begin = System.currentTimeMillis();
        list.forEach(e -> {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            e.toString();
        });
        end = System.currentTimeMillis();
        System.out.println("普通lambda表达式foreach耗时" + (end - begin) + "ms");

        begin = System.currentTimeMillis();
        list.stream().forEach(e -> {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            e.toString();
        });
        end = System.currentTimeMillis();
        System.out.println("单管道stream耗时" + (end - begin) + "ms");

        begin = System.currentTimeMillis();
        list.parallelStream().forEach(e -> {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            e.toString();
        });
        end = System.currentTimeMillis();
        System.out.println("多管道stream耗时" + (end - begin) + "ms");
    }
}