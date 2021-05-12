package pers.mihao.ancient_empire.base.dao;

import org.apache.ibatis.annotations.Mapper;

/**
 * @Author mh32736
 * @Date 2021/5/9 10:07
 */
@Mapper
public interface AeSequenceDAO {

    int getNewId(String tableName);

    void increase(String tableName);

}
