package com.mihao.ancient_empire;

import com.mihao.ancient_empire.common.vo.test_dto.Dog;
import com.mihao.ancient_empire.constant.CollectionEnum;
import com.mihao.ancient_empire.constant.MapEnum;
import com.mihao.ancient_empire.entity.mongo.UserMap;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;;
import com.mihao.ancient_empire.mongo.dao.UserMapRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MongoTest {

    @Autowired
    UserMapRepository userMapRepository;

    @Autowired
    MongoTemplate mongoTemplate;


    @Test
    public void updateMapType() {
        Update update = new Update();
        update.set("type", MapEnum.ENCOUNTER.getType());
        mongoTemplate.upsert(new Query(Criteria.where("mapName").is("远古要塞")), update, CollectionEnum.USER_MAP.getType());
        UserMap userMap = mongoTemplate.findOne(new Query(Criteria.where("mapName").is("远古要塞")), UserMap.class, CollectionEnum.USER_MAP.getType());
        System.out.println(userMap.getType());
    }

    @Test
    public void name() {
        List<UserMap> userMapList = userMapRepository.findAll();
        System.out.println(userMapList.size());
    }

    @Test
    public void update() {
        Dog dog1 = mongoTemplate.findOne(new Query(Criteria.where("name").is("大黄")), Dog.class, "dog");
        Dog dog2 = mongoTemplate.findOne(new Query(Criteria.where("name").is("小红")), Dog.class, "dog");
        Update update = new Update();
        update.set("name", "小红");
        mongoTemplate.updateFirst(new Query(Criteria.where("name").is("大黄")), update, "dog");
    }

    @Test
    public void getAndSave() {
        String dog = "dog";
        Query query = new Query(Criteria.where("id").is(1));
        System.out.println(mongoTemplate.exists(query, dog));
        mongoTemplate.insert(Dog.getList(), dog);
        Dog dog1 = mongoTemplate.findOne(new Query(Criteria.where("name").is("大黄")), Dog.class, dog);
        System.out.println(mongoTemplate.exists(query, dog));
        Update update = new Update();
        update.set("name", "小红");
        mongoTemplate.updateFirst(new Query(Criteria.where("name").is("大黄")), update, dog);
        Dog dog2 = mongoTemplate.findOne(new Query(Criteria.where("name").is("大黄")), Dog.class, dog);
        System.out.println(dog2);
    }
}
