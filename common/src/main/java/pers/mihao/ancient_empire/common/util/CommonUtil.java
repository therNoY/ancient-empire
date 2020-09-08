package pers.mihao.ancient_empire.common.util;

import java.lang.reflect.Field;

public class CommonUtil {

    /**
     * 判断对象是否有空属性
     * @param object
     * @return
     */
    public static boolean isHaveNull (Object object) {
        Class clazz = object.getClass();
        Field[] fields = clazz.getDeclaredFields();
        boolean isHaveNull = false;
        if (fields.length == 0){
            return isHaveNull;
        }
        for(int i = 0 ;i < fields.length ;i++){
            if(isHaveNull){
                return isHaveNull;
            }
            Field field = fields[i];
            field.setAccessible(true);
            String fieldName = field.getName();
            Object fieldValue = null;
            try {
                fieldValue = field.get(object);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if(fieldValue == null){
                isHaveNull = true;
            }
        }
        return isHaveNull;
    }
}