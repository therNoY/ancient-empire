package pers.mihao.ancient_empire.startup;

import com.mongodb.client.result.UpdateResult;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.test.context.junit4.SpringRunner;
import pers.mihao.ancient_empire.base.bo.Army;
import pers.mihao.ancient_empire.base.bo.Position;
import pers.mihao.ancient_empire.base.dao.mongo.UserRecordRepository;
import pers.mihao.ancient_empire.base.entity.UserRecord;
import pers.mihao.ancient_empire.core.util.UserRecordMongoHelper;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserRecordMongoHelperTest {

    @Autowired
    UserRecordMongoHelper userRecordMongoHelper;
    @Autowired
    UserRecordRepository userRecordRepository;
    @Autowired
    MongoTemplate mongoTemplate;

    @Test
    public void name() {
        String id = "9e6d799da47f452a8afa9c02907b26ad";
//        UserRecord userRecord = userRecordRepository.findById(id).get();
//        mongoHelper.addRecordTomb(id, new Position(10,10));
        Position tomb = new Position(9,7);
        Query query = Query.query(Criteria.where("_id").is(id));
        Update update = new Update().pull("tomb", tomb);
        UpdateResult result = mongoTemplate.updateFirst(query, update, UserRecord.class);
        System.out.println(result.getMatchedCount());
//        List<Army> armyList = userRecord.getArmyList();
//        for (Army army: armyList) {
//            if (army.getColor().equals("blue")) {
//                army.setMoney(army.getMoney() + 100);
//                break;
//            }
//        }
//        userRecordMongoHelper.updateArmy(id, armyList);
//
//        UserRecord userRecord2 = userRecordRepository.findById(id).get();
//        System.out.println(userRecord2.getCurrColor());

    }


    @Test
    public void name2() {
        String id = "e2e97453b4094c42a42d1936a528e45b";
        UserRecord userRecord = userRecordRepository.findById(id).get();
        List<Army> armyList = userRecord.getArmyList();


        long start = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
//            userRecordMongoHelper.updateArmy(id, armyList);
        }
        System.out.println(System.currentTimeMillis() - start);

        long start3 = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            userRecordRepository.deleteById(id);
            userRecord = userRecordRepository.save(userRecord);
        }
        System.out.println(System.currentTimeMillis() - start3);



        long start1 = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            userRecordMongoHelper.updateRecord(userRecord);
        }
        System.out.println(System.currentTimeMillis() - start1);

    }

    @Test
    public void name4() {
        String id = "e2e97453b4094c42a42d1936a528e45b";
        UserRecord userRecord = userRecordRepository.findById(id).get();
        List<Army> armies = userRecord.getArmyList();
        armies.get(0).setMoney(500);
//        userRecordMongoHelper.updateArmy(id, armies);
    }

    @Test
    public void name5() {
        String id = "e2e97453b4094c42a42d1936a528e45b";
        UserRecord userRecord = userRecordRepository.findById(id).get();
        System.out.println(userRecord.getCurrColor());
        long start3 = System.currentTimeMillis();
//        userRecordMongoHelper.updateColor(id, "red");
        System.out.println(System.currentTimeMillis() - start3);
        UserRecord userRecord2 = userRecordRepository.findById(id).get();
        System.out.println(userRecord2.getCurrColor());

        long start4 = System.currentTimeMillis();
        userRecordRepository.deleteById(id);
        userRecord2.setCurrColor("blue");
        userRecordRepository.save(userRecord2);
        System.out.println(System.currentTimeMillis() - start4);
    }
}
