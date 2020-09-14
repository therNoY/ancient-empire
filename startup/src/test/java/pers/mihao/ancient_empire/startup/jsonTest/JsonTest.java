package pers.mihao.ancient_empire.startup.jsonTest;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import pers.mihao.ancient_empire.common.util.JacksonUtil;
import pers.mihao.ancient_empire.common.vo.test_dto.Dog;

import java.util.List;

public class JsonTest {
    @Test
    public void name() {
        List<Dog> dogList = Dog.getList();
        String json = JSON.toJSONString(dogList);
        System.out.println(json);
        List<Dog> dogs = JacksonUtil.jsonToList(json, Dog.class);
        System.out.println(dogs.size());
    }
}
