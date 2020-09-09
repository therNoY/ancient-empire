package pers.mihao.ancient_empire.base.dao.mongo;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import pers.mihao.ancient_empire.base.entity.mongo.UserMap;

@Repository
public interface UserMapRepository extends MongoRepository<UserMap, String> {

    List<UserMap> findByCreateUserId(Integer id);

    void deleteByUnSaveAndCreateUserId(boolean unSave, Integer id);

    UserMap getFirstByCreateUserIdAndMapName(Integer id, String name);

    void deleteByCreateUserIdAndUuid(Integer id, String uuid);

    List<UserMap> getAllByCreateUserIdAndType(Integer id, String type);

    UserMap getFirstByUuid(String uuid);

}
