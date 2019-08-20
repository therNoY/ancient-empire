package com.mihao.ancient_empire.mongo.dao;

import com.mihao.ancient_empire.entity.mongo.UserMap;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserMapRepository extends MongoRepository<UserMap, String> {

    List<UserMap> findByCreateUserId(Integer id);

    void deleteByUnSaveAndCreateUserId(boolean unSave, Integer id);

    UserMap getFirstByCreateUserIdAndMapName(Integer id, String name);

    void deleteByCreateUserIdAndUuid(Integer id, String uuid);

    List<UserMap> getAllByCreateUserIdAndType(Integer id, String type);

    UserMap getFirstByUuid(String uuid);
}
