package pers.mihao.ancient_empire.common.util;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import pers.mihao.ancient_empire.common.dto.GetSetDTO;
import pers.mihao.ancient_empire.common.vo.AncientEmpireException;
import sun.misc.Unsafe;

/**
 * @version 1.0
 * @author mihao
 * @date 2020\9\20 0020 11:14
 */
public class ReflectUtil {

    private static final String GET = "get";

    private static final String SET = "set";

    private static Map<Class, Map<String, GetSetDTO>> getSetMap = new HashMap<>(16);


    private ReflectUtil() {
    }

    /**
     * 获取字段对应值，并转为String类型，空值返回空字符串
     *
     * @param fieldName
     * @param obj
     * @return
     */
    public static synchronized String getStringValue(String fieldName, Object obj) throws ReflectiveOperationException {
        Object objectValue = getValueByGetter(fieldName, obj);
        if (objectValue == null) {
            return "";
        }
        String result = objectValue.toString();
        //如果类型为BigDecimal,去掉末尾的0
        if (objectValue instanceof BigDecimal) {
            BigDecimal value = (BigDecimal) objectValue;
            value = value.stripTrailingZeros();
            result = value.toPlainString();
        } else if (objectValue instanceof Date) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            result = sdf.format((Date) objectValue).replace(" 00:00:00", "");
        }

        return result.trim();
    }

    public static Object getValueByGetter(String fieldName, Object obj) throws ReflectiveOperationException {
        Method getter = getGetter(fieldName, obj.getClass());
        if (getter != null) {
            return getter.invoke(obj);
        }

        return null;
    }


    public static Object setValueBySetter(String fieldName, Object obj) throws ReflectiveOperationException {
        Method setter = getSetter(fieldName, obj.getClass());
        if (setter == null) {
            throw new ReflectiveOperationException("没有set方法");
        }

        return setter.invoke(obj);
    }

    /**
     * 获取set方法
     *
     * @param fieldName
     * @param cls
     * @return
     */
    public static Method getSetter(String fieldName, Class<?> cls) {
        for (Method method : cls.getMethods()) {
            if (method.getName().equalsIgnoreCase("set".concat(fieldName)) && method.getParameterTypes().length == 0) {
                return method;
            }
        }
        return null;
    }

    /**
     * 通过属性名获取Field对象
     *
     * @param fieldName
     * @param cls
     * @return
     */
    public static Field getFieldByName(String fieldName, Class<?> cls)  {
        Field field = null;
        for (; cls != Object.class; cls = cls.getSuperclass()) {
            try {
                field = cls.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
            }
            if (field != null) {
                return field;
            }
        }
        return field;
    }

    /**
     * 通过对象.class获取所有Fields，包括父类
     *
     * @param clazz
     * @return
     */
    public static List<Field> listFields(Class<?> clazz) {
        Field[] fs = clazz.getDeclaredFields();
        List<Field> fields = new ArrayList<>(Arrays.asList(fs));
        if (clazz.getSuperclass() != null) {
            fields.addAll(listFields(clazz.getSuperclass()));
        }
        return fields;
    }

    public static boolean fieldExist(String fieldName, Class<?> cls) {
        return getFieldByName(fieldName, cls) != null;
    }


    /**
     * 反射设置对象的字段值
     * @param object
     * @param filedName
     * @param value
     */
    public static void setValueBySetMethod(Object object, String filedName, String value){
        Method method = getSetter(filedName, object.getClass());
        try {
            method.invoke(object, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取字段对应的get方法
     *
     * @param fieldName
     * @param clazz
     * @return
     */
    public static Method getGetter(String fieldName, Class clazz) {
        String methodName = GET.concat(getMethodField(fieldName));
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName) && method.getParameterTypes().length == 0) {
                return method;
            }
        }
        return null;
    }

    /**
     * 获取一个类的所有getset
     *
     * @param clazz
     * @return
     */
    public static Map<String, GetSetDTO> getAllGetSetMethod(Class clazz) {
        Map<String, GetSetDTO> all;
        if ((all = getSetMap.get(clazz)) == null) {
            all = new HashMap<>();
            List<Field> fs = listFields(clazz);
            Method[] methods = clazz.getMethods();
            String getName, setName;
            for (Field field : fs) {
                Method get = null, set = null;
                getName = GET.concat(getMethodField(field.getName()));
                setName = SET.concat(getMethodField(field.getName()));
                for (Method method : methods) {
                    if (get != null && set != null) {
                        break;
                    } else {
                        if (get == null && method.getName().equals(getName)) {
                            get = method;
                            continue;
                        }
                        if (set == null && method.getName().equals(setName)) {
                            set = method;
                        }
                    }
                }
                if (set != null && get != null) {
                    all.put(field.getName(), new GetSetDTO(get, set));
                }
            }
            getSetMap.put(clazz, all);
        }
        return all;
    }

    private static String getMethodField(String name) {
        if (name != null && name.length() > 0) {
            return Character.toUpperCase(name.charAt(0)) + name.substring(1);
        }
        throw new AncientEmpireException();
    }



    private static Unsafe getUnsafe() {
        try {
            Field unsafeStaticField =
                    Unsafe.class.getDeclaredField("theUnsafe");
            unsafeStaticField.setAccessible(true);
            return (Unsafe) unsafeStaticField.get(Unsafe.class);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        throw new AncientEmpireException();
    }

    /**
     * 通过反射获取对象
     * @param obj
     * @param delegate
     * @return
     */
    public static Object getValueByFieldName(Object obj, String delegate) {
        Field filed = getFieldByName(delegate, obj.getClass());
        return getValueByField(obj, filed);
    }

    /**
     * 通过反射获取对象
     * @param obj
     * @param delegate
     * @return
     */
    public static Object getValueByField(Object obj, Field filed) {
        Object value = null;
        if (filed != null) {
            try {
                if (filed.isAccessible()) {
                    value = filed.get(obj);
                }else {
                    filed.setAccessible(true);
                    value = filed.get(obj);
                    filed.setAccessible(false);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return value;
    }

    /**
     * 反射设置值
     * @param object
     * @param fieldName
     * @param fieldValue
     */
    public static void setValueByFieldName(Object object, String fieldName, Object fieldValue) {
        Field field = getFieldByName(fieldName, object.getClass());
        if (field != null) {
            try {
                if (field.isAccessible()) {
                    field.set(object, fieldValue);
                }else {
                    field.setAccessible(true);
                    field.set(object, fieldValue);
                    field.setAccessible(false);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
