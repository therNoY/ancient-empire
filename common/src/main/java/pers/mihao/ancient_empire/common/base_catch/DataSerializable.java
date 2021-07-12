package pers.mihao.ancient_empire.common.base_catch;

/**
 * @Author mh32736
 * @Date 2021/7/12 15:44
 */
public interface DataSerializable<T> {

    /**
     * 序列化对象
     *
     * @param catchData
     * @param <T>
     * @return
     */
    Object serialObj(T catchData);

    /**
     * 反序列化
     *
     * @param obj
     * @return
     */
    Object unSerialObj(Object obj, Class clazz);
}
