package pers.mihao.ancient_empire.common.jdbc.mongo;

import pers.mihao.ancient_empire.common.jdbc.mongo.annotation.QueryField;
import pers.mihao.ancient_empire.common.util.ReflectUtil;
import pers.mihao.ancient_empire.common.util.StringUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @version 1.0
 * @author mihao
 * @date 2020\9\20 0020 11:02
 */
public class MongodbConverter {

    Map<Class, List<String>> queryFieldMap = new HashMap<>();

    /**
     * 通过反射获取对象需要查询的字段
     * @param returnClass
     * @param <T>
     * @return
     */
    public <T> List<String> getQueryFieldByClass(Class<T> returnClass) {
        List<String> res;
        if ((res = queryFieldMap.get(returnClass)) != null) {
            return res;
        }
        res = new ArrayList<>();
        Field[] fields = returnClass.getDeclaredFields();

        for (Field field : fields) {
            QueryField fieldQuery = field.getAnnotation(QueryField.class);
            if (fieldQuery == null) {
                Method method = ReflectUtil.getGetter(field.getName(), returnClass);
                if (method != null) {
                    QueryField methodQuery = method.getAnnotation(QueryField.class);
                    if (methodQuery != null && methodQuery.query() && StringUtil.isBlack(methodQuery.value())) {
                        res.add(methodQuery.value());
                    }else if (methodQuery == null || methodQuery.query()) {
                        res.add(field.getName());
                    }
                }
            }else {
                if (fieldQuery.query()) {
                    res.add(StringUtil.isBlack(fieldQuery.value()) ? field.getName() : fieldQuery.value());
                }
            }
        }
        queryFieldMap.put(returnClass, res);
        return res;
    }
}
