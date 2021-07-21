package pers.mihao.ancient_empire.common.base_catch;

/**
 * @Author mihao
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
     * @param clazz
     * @param isArray
     * @return
     */
    Object unSerialObj(Object obj, Class clazz, boolean isArray);
}
