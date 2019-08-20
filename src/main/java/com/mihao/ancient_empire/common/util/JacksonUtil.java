package com.mihao.ancient_empire.common.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;



public class JacksonUtil {

    private static Logger log = LoggerFactory.getLogger(JacksonUtil.class);

    private static ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.enable(SerializationFeature.INDENT_OUTPUT); //化化输出
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS); //序列化空的 POPJ
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); //不将date转化成timestamp
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES); //忽略未知属性
        mapper.disable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT); //不将空转化为null
        mapper.disable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES); //允许没有引号的字段名（非标准）
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    }

    public static ObjectMapper getDefaultMapper() {
        return mapper;
    }

    public static <T> String toJson(T t) {
        return toJson(t, null);
    }

    public static <T> T jsonTo(String json, TypeReference<T> typeReference) {
        return jsonTo(json, typeReference, null);
    }

    public static <T> T jsonToBean(String json, Class<T> clazz) {
        return jsonToBean(json, clazz, null);
    }

    /**
     * 将json 转成 List
     * @param json
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> List<T> jsonToList(String json, Class<T> clazz) {
        ObjectMapper mapper = new ObjectMapper();
        JavaType javaType = getCollectionType(mapper, ArrayList.class, clazz);
        List<T> list = null;
        try {
            list  =  mapper.readValue(json, javaType);   //这里不需要强制转换
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static <T> List<T> jsonToList(String json, Class<T> clazz, Boolean userDefault) {
        if (!userDefault) {
            return jsonToList(json, clazz);
        }
        JavaType javaType = getCollectionType(mapper, ArrayList.class, clazz);
        List<T> list = null;
        try {
            list  =  mapper.readValue(json, javaType);   //这里不需要强制转换
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static JavaType getCollectionType(ObjectMapper mapper, Class<?> collectionClass, Class<?>... elementClasses) {
        return mapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }

    public static <T> T fileToBean(File file, Class<T> clazz) {
        return fileToBean(file, clazz, null);
    }

    public static <T> String toJson(T t, ObjectMapper objectMapper) {
        if (Objects.isNull(t))
            return null;
        try {
            return objectMapper == null ? mapper.writeValueAsString(t) : objectMapper.writeValueAsString(t);
        } catch (JsonProcessingException e) {
            log.error("to json error:" + t, e);
            return null;
        }
    }

    public static <T> T jsonToBean(String json, Class<T> clazz, ObjectMapper objectMapper) {
        if (StringUtil.isEmpty(json))
            return null;
        try {
            return objectMapper == null ? mapper.readValue(json, clazz) : objectMapper.readValue(json, clazz);
        } catch (IOException e) {
            log.error("json to bean error:" + json, e);
            return null;
        }
    }

    public static <T> T fileToBean(File file, Class<T> clazz, ObjectMapper objectMapper) {
        if (!file.exists())
            return null;
        try {
            return objectMapper == null ? mapper.readValue(file, clazz) : objectMapper.readValue(file, clazz);
        } catch (IOException e) {
            log.error("filter to bean error:" + file.getName(), e);
            return null;
        }
    }

    public static <T> T jsonTo(String json, TypeReference<T> typeReference, ObjectMapper objectMapper) {
        if (StringUtil.isEmpty(json))
            return null;
        try {
            return objectMapper == null ? mapper.readValue(json, typeReference) : objectMapper.readValue(json, typeReference);
        } catch (Exception e) {
            log.error("json to map error:" + json, e);
            return null;
        }
    }

    public static <T> T jsonTo(String json, Class<T> clazz, ObjectMapper objectMapper) {
        if (StringUtil.isEmpty(json))
            return null;
        try {
            return mapper.readValue(json, new TypeReference<T>(){});
        } catch (Exception e) {
            log.error("json to map error:" + json, e);
            return null;
        }
    }
}