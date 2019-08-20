package com.mihao.ancient_empire.java_base;

import com.mihao.ancient_empire.common.vo.test_dto.Dog;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

public class ListStreamTest {

    @Test
    public void name() {
        List<Dog> dogList = Dog.getList();
        List newDog = dogList.stream().filter(dog -> dog.getId() == 2).collect(Collectors.toList());
        System.out.println(newDog.size());
    }
}
