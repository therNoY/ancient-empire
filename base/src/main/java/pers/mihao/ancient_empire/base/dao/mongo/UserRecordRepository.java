package pers.mihao.ancient_empire.base.dao.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import pers.mihao.ancient_empire.base.entity.UserRecord;


@Repository
public interface UserRecordRepository extends MongoRepository<UserRecord, String> {

    UserRecord getFirstByCreateUserIdAndRecordName(Integer id, String name);

    void deleteByUnSaveAndCreateUserId(boolean unSave, Integer id);

}
