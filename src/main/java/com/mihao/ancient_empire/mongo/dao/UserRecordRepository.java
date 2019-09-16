package com.mihao.ancient_empire.mongo.dao;

import com.mihao.ancient_empire.entity.mongo.UserRecord;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRecordRepository extends MongoRepository<UserRecord, String> {

}
