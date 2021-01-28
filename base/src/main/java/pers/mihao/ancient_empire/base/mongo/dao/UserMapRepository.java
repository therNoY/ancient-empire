package pers.mihao.ancient_empire.base.mongo.dao;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import pers.mihao.ancient_empire.base.entity.UserMap;

@Repository
public interface UserMapRepository extends MongoRepository<UserMap, String> {

    List<UserMap> findByCreateUserId(Integer id);

    /**
     * 获取用户的草稿地图
     * @param userId
     * @param unSave
     * @return
     */
    UserMap getFirstByCreateUserIdAndUnSave(Integer userId, Boolean unSave);

    UserMap getFirstByCreateUserIdAndMapName(Integer id, String name);

    void deleteByCreateUserIdAndUuid(Integer id, String uuid);


    UserMap getFirstByUuid(String uuid);

}
