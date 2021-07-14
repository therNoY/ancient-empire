package pers.mihao.ancient_empire.base.dao;

import org.apache.ibatis.annotations.Mapper;

/**
 * @Author mihao
 * @Date 2021/5/9 10:07
 */
@Mapper
public interface AeSequenceDAO {

    /**
     * 获取一个新的序列ID
     * @param tableName
     * @return
     */
    int getNewId(String tableName);

    /**
     * 自增
     * @param tableName
     */
    void increase(String tableName);

}
