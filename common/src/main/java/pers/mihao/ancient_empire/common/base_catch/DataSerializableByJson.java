package pers.mihao.ancient_empire.common.base_catch;

import com.alibaba.fastjson.JSONObject;

/**
 * @Author mh32736
 * @Date 2021/7/12 15:43
 */
public class DataSerializableByJson<T> implements DataSerializable<T> {


    @Override
    public Object serialObj(Object catchData) {
        return JSONObject.toJSONString(catchData);
    }

    @Override
    public Object unSerialObj(Object obj, Class clazz) {
        return JSONObject.parseObject(obj.toString(), clazz);
    }
}
