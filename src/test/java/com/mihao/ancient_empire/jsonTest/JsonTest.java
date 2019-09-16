package com.mihao.ancient_empire.jsonTest;

import com.mihao.ancient_empire.common.util.JacksonUtil;
import com.mihao.ancient_empire.common.vo.test_dto.Dog;
import org.junit.Test;

import java.util.List;

public class JsonTest {
    @Test
    public void name() {
        List<Dog> dogList = Dog.getList();
        String json = JacksonUtil.toJson(dogList);
        System.out.println(json);
        List<Dog> dogs = JacksonUtil.jsonToList(json, Dog.class);
        System.out.println(dogs.size());
    }
}
