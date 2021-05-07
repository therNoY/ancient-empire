package pers.mihao.ancient_empire.common.util;

import pers.mihao.ancient_empire.common.dto.GetSetDTO;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import pers.mihao.ancient_empire.common.vo.AncientEmpireException;

/**
 * @version 1.0
 * @author mihao
 * @date 2020\10\3 0003 8:23
 */
public class BeanUtil {

    /**
     * 从父类拷贝值到子类
     *
     * @param from  父类
     * @param clazz 子类的类对象
     * @param <T>
     */
    public static <T, M> M copyValueFromParent(T from, M to) {
        try {
            Map<String, GetSetDTO> toMap = ReflectUtil.getAllGetSetMethod(to.getClass());
            Map<String, GetSetDTO> fromMap = ReflectUtil.getAllGetSetMethod(from.getClass());
            for (Map.Entry<String, GetSetDTO> entry : fromMap.entrySet()) {
                GetSetDTO pair = entry.getValue();
                GetSetDTO cPair = toMap.get(entry.getKey());
                cPair.getSet().invoke(to, pair.getGet().invoke(from));
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return to;
    }

    /**
     * 从父类拷贝值到子类
     *
     * @param t     父类
     * @param clazz 子类的类对象
     * @param <T>
     */
    public static <T, M> M copyValueFromParent(T t, Class<M> clazz) {
        M res = null;
        if (t == null) {
            return null;
        }
        try {
            res = clazz.newInstance();
            Map<String, GetSetDTO> cMap = ReflectUtil.getAllGetSetMethod(clazz);
            Map<String, GetSetDTO> pMap = ReflectUtil.getAllGetSetMethod(t.getClass());
            for (Map.Entry<String, GetSetDTO> entry : pMap.entrySet()) {
                GetSetDTO pair = entry.getValue();
                GetSetDTO cPair = cMap.get(entry.getKey());
                cPair.getSet().invoke(res, pair.getGet().invoke(t));
            }
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return res;
    }


    public static <T> T deptClone(T t) {
        try {
            Object obj = t.getClass().newInstance();
            List<Field> list = ReflectUtil.listFields(t.getClass());
            for (Field field : list) {
                ReflectUtil.setValueByFieldName(obj, field.getName(), ReflectUtil.getValueByField(t, field));
            }
            return (T) obj;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new AncientEmpireException(e.getMessage());
        }
    }

    /**
     * 从一个类copy到另一个类
     *
     * @param from  目标类
     * @param to 结果类
     * @param <T>
     */
    public static void copyValueByGetSet(Object from, Object to) {
        try {
            Map<String, GetSetDTO> toMap = ReflectUtil.getAllGetSetMethod(to.getClass());
            Map<String, GetSetDTO> fromMap = ReflectUtil.getAllGetSetMethod(from.getClass());
            GetSetDTO fromMethod;
            GetSetDTO toMethod;
            for (Map.Entry<String, GetSetDTO> entry : fromMap.entrySet()) {
                fromMethod = entry.getValue();
                Object res;
                if ((toMethod = toMap.get(entry.getKey())) != null && (res = fromMethod.getGet().invoke(from))!= null) {
                    try {
                        toMethod.getSet().invoke(to, res);
                    } catch (IllegalArgumentException e) {
                    }
                }
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }


    /**
     * 从子类拷贝值到父类
     *
     * @param t
     * @param clazz
     * @param <T>
     */
    public static <T> Object copyValueToParent(T t, Class<? super T> clazz) {
        Object res = null;
        try {
            res = clazz.newInstance();
            Map<String, GetSetDTO> map = ReflectUtil.getAllGetSetMethod(clazz);
            for (Map.Entry<String, GetSetDTO> entry : map.entrySet()) {
                GetSetDTO pair = entry.getValue();
                pair.getSet().invoke(res, pair.getGet().invoke(t));
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return res;
    }

}
