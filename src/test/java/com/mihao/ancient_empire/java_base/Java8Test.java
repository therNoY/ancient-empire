package com.mihao.ancient_empire.java_base;

import com.mihao.ancient_empire.common.vo.test_dto.Dog;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Java8Test {

    @Test
    public void testConsumer() {
        Consumer<Dog> consumer = dog -> {
            System.out.println(dog.getId() + ": " + dog.getName());
        };


        consumer.accept(new Dog(1, "哒小黄"));
    }


    @Test
    public void testFunction() {
        Function<Integer, Integer> function = n -> {
            int newN = n * 10;
            return newN;
        };

        System.out.println(function.apply(10));
    }

    @Test
    public void testSupplier() {
        Supplier<Dog> supplier = () -> new Dog();
        System.out.println(supplier.get());
    }

    @Test
    public void testPredicate() {
        Predicate<Dog> predicate = dog -> {
            if (dog.getName().length() > 2) {
                return true;
            }
            return false;
        };
        System.out.println(predicate.test(new Dog(1, "22")));
    }


    @Test
    public void name() {
        List<String> stringList = Arrays.asList("1", "2", "3", "4");


        System.out.println(stringList.stream().findAny().get());
        System.out.println(stringList.stream().findAny().get());
        System.out.println(stringList.stream().findAny().get());
    }
}
