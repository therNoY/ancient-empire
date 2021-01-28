package pers.mihao.ancient_empire.startup.mongo;

import com.alibaba.fastjson.JSON;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import org.bson.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.test.context.junit4.SpringRunner;
import pers.mihao.ancient_empire.base.bo.BaseUnit;
import pers.mihao.ancient_empire.base.mongo.dao.UserMapRepository;
import pers.mihao.ancient_empire.base.entity.UserMap;
import pers.mihao.ancient_empire.base.enums.CollectionEnum;
import pers.mihao.ancient_empire.base.enums.GameTypeEnum;
import pers.mihao.ancient_empire.base.service.UnitMesService;
import pers.mihao.ancient_empire.common.util.JacksonUtil;
import pers.mihao.ancient_empire.common.vo.test_dto.Dog;

;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MongoTest {

    @Autowired
    UserMapRepository userMapRepository;

    @Autowired
    UnitMesService unitMesService;

    @Autowired
    MongoTemplate mongoTemplate;


    @Test
    public void updateMapType() {
        Update update = new Update();
        update.set("type", GameTypeEnum.ENCOUNTER.type());
        mongoTemplate.upsert(Query.query(Criteria.where("mapName").is("远古要塞")), update, CollectionEnum.USER_MAP.type());
        UserMap userMap = mongoTemplate.findOne(new Query(Criteria.where("mapName").is("远古要塞")), UserMap.class, CollectionEnum.USER_MAP.type());
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


    @Test
    public void write() throws IOException {
        List<UserMap> userMaps = userMapRepository.findAll();
        File file = new File("D:/userMap.txt");

        String j = JSON.toJSONString(userMaps);

        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));


        bufferedWriter.write(j);

        bufferedWriter.close();
    }

    @Test
    public void Reader() throws IOException {
        File file = new File("D:/userMap.txt");
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        String info = bufferedReader.readLine();

        List<UserMap> userMaps = JacksonUtil.jsonToList(info, UserMap.class);
        userMaps.forEach(userMap -> userMapRepository.save(userMap));
    }

    public static void main(String[] ag) {
        MongoClient mongoClient = new MongoClient("192.168.43.121", 27017);
        MongoDatabase mongoDatabase = mongoClient.getDatabase("ancient-empire");
        MongoCollection<Document> de =  mongoDatabase.getCollection("dog");
        de.listIndexes().forEach((Block<? super Document>) a->{
            System.out.println(a);
        });

        mongoClient.close();
    }
}
