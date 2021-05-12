package pers.mihao.ancient_empire.base.service;

/**
 * @Author mh32736
 * @Date 2021/5/9 10:14
 */
public interface AeSequenceService {

    /**
     * 获取新的id
     * @param name
     * @return
     */
    int getNewIdByType(String name);

}
