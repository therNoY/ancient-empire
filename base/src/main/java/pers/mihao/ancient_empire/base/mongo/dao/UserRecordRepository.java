package pers.mihao.ancient_empire.base.mongo.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import pers.mihao.ancient_empire.base.entity.UserRecord;

import java.util.List;


@Repository
public interface UserRecordRepository extends MongoRepository<UserRecord, String> {

    UserRecord getFirstByCreateUserIdAndRecordName(Integer id, String name);

    void deleteByUnSaveAndCreateUserId(boolean unSave, Integer id);

}
