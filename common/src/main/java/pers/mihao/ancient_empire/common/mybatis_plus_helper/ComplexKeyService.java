package pers.mihao.ancient_empire.common.mybatis_plus_helper;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Author mihao
 * @Date 2021/4/29 22:27
 */
public interface ComplexKeyService<T> extends IService<T> {

    /**
     * 根据联合主键查询
     * @param t
     * @return
     */
    T selectByPrimaryKey(T t);


    /**
     * 根据主键删除
     * @param t
     * @return
     */
    int deleteByPrimaryKey(T t);

}
