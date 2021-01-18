package pers.mihao.ancient_empire.common.util;

import pers.mihao.ancient_empire.common.annotation.ValidatedBean;
import pers.mihao.ancient_empire.common.vo.AncientEmpireException;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @version 1.0
 * @auther mihao
 * @date 2021\1\17 0017 19:26
 */
public class ValidateUtil {
    /**
     * 验证对象
     * @param object
     */
    public static void validateBean(Object object) {
        // 验证
        ValidatedBean validatedBean = object.getClass().getAnnotationsByType(ValidatedBean.class)[0];
        String[] notNullFileName = validatedBean.notNull();
        validatedBean(object, notNullFileName);
    }

    private static void validatedBean(Object object, String[] notNullFileName) {
        List<String> notNullFieldNameList = Arrays.stream(notNullFileName).collect(Collectors.toList());

        Field[] fields = object.getClass().getDeclaredFields();
        Object value;
        for (Field field : fields) {
            if (notNullFieldNameList.contains("*") || notNullFieldNameList.contains(field.getName())) {
                validatedFileNotNull(object, field);
            }

            if (field.getAnnotationsByType(ValidatedBean.class).length > 0) {
                if ((value = ReflectUtil.getValueByField(object, field)) != null) {
                    validatedBean(value, field.getAnnotationsByType(ValidatedBean.class)[0].notNull());
                }
            }
        }


    }

    private static void validatedFileNotNull(Object object, Field field) {
        Object value = ReflectUtil.getValueByField(object, field);
        if (value instanceof String) {
            if (value == null || StringUtil.isBlack(value.toString())) {
                throw new AncientEmpireException("字段:" + field.getName() + "不能为空");
            }
        }else {
            if (value == null) {
                throw new AncientEmpireException("字段:" + field.getName() + "不能为空");
            }
        }
    }

}
