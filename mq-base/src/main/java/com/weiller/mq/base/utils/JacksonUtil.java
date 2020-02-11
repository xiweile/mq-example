package com.weiller.mq.base.utils;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 */
public class JacksonUtil {

    private static ObjectMapper mapper;

    private final static Logger LOG = LoggerFactory.getLogger(JacksonUtil.class);


    public static <T> T jsonToBean(String jsonStr, Class<T> cls) {
    	if(StringUtils.isEmpty(jsonStr)){
    		return null;
    	}
        try {
            return getMapper().readValue(jsonStr, cls);
        } catch (IOException e) {
            LOG.error("json to Bean 转换失败，{}", e);
            return null;
        }

    }

    private static ObjectMapper getMapper() {
        if (mapper == null) {
            mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
            mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
            mapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
        }
        return mapper;
    }

    public static String beanToJson(Object src) {
        // Convert object to JSON string
        try {
            return getMapper().writeValueAsString(src);
        } catch (JsonProcessingException e) {
            LOG.error("bean to json 失败，{}", e);
            return null;
        }

    }
 
    /**
     * beanToBean
     * @param ob
     * @param t
     * @return T
     */
    public static <T>T  beanToBean(Object ob, Class<T> t){
        String json = toJson(ob);
        return fromJson(json, t);
    }
    
    /**
     * 
     * beanToBean
     * @param ob
     * @param t
     * @return T
     */
    public static <T>T  beanToBean(Object ob, T t){
        BeanUtils.copyProperties(ob, t);
        return t;
    }
    
    public static String toJson(Object object) {
        try {
            return getMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("序列化为JSON失败", e);
        }
    }

    public static <T> T fromJson(InputStream inputStream, Class<T> klass) {
        String json;
        try {
            json = CharStreams.toString(new InputStreamReader(inputStream, Charsets.UTF_8));
        } catch (IOException e) {
            throw new IllegalStateException("读取输入流失败", e);
        }
        JavaType javaType = getMapper().getTypeFactory().constructType(klass);
        return fromJsonViaJavaType(json, javaType);
    }
    public static <T> T fromJson(String json, Class<T> klass) {
        JavaType javaType = getMapper().getTypeFactory().constructType(klass);
        return fromJsonViaJavaType(json, javaType);
    }
    
    private static <T> T fromJsonViaJavaType(String json, JavaType javaType) {
        try {
            return getMapper().readerFor(javaType).readValue(json);
        } catch (IOException e) {
            throw new IllegalStateException("反序列化失败", e);
        }
    }

    public static <T> T fromJson(String json, Type type) {
        JavaType javaType = getMapper().getTypeFactory().constructType(type);
        return fromJsonViaJavaType(json, javaType);
    }
    
    public static JavaType constructType(Type type) {
        return getMapper().getTypeFactory().constructType(type);
    }

    public static Map<String, Object> jsonToMap(String json) {
        return jsonToMap(json, String.class, Object.class);
    }

    public static <K, V> Map<K, V> jsonToMap(String json, Class<K> kClass, Class<V> vClass) {
        try {
            JavaType kType = getMapper().getTypeFactory().constructType(kClass);
            JavaType vType = getMapper().getTypeFactory().constructType(vClass);
            MapType mapType = getMapper().getTypeFactory().constructMapType(HashMap.class, kType, vType);
            return getMapper().readValue(json, mapType);
        } catch (IOException e) {
            throw new IllegalStateException("反序列化失败", e);
        }
    }

    public static <T> List<T> fromJsonArray(String json, Class<T> clazz) {
        CollectionType javaType = getMapper().getTypeFactory().constructCollectionType(List.class, clazz);
        return fromJsonViaJavaType(json, javaType);
    }
    
    public static ObjectReader readerFor(JavaType javaType) {
        return mapper.readerFor(javaType);
    }
    
    
    public static <T> List<T> jsonStrArrToListBean(String jsonStrArr, Class<T> t) {
        List<?> jsonArr= jsonToBean(jsonStrArr, List.class);
        return arr2ListBean(jsonArr,t);
    }
    public static <T> List<T> mapToListBean(Object mapObj, Class<T> t) {
        String jsonStrArr = toJson(mapObj);
    	List<?> jsonArr = fromJson(jsonStrArr, List.class);
    	return arr2ListBean(jsonArr,t);
    }
    public static <T> List<T> arr2ListBean(List<?> src_ls, Class<T> t){
        List<T> mxLs=new ArrayList<T>();
        for(Object obj :src_ls){
            T tmpObj= mapToJavaBean(obj,t);
            mxLs.add(tmpObj);
        }
        return mxLs;
    }
    
    public static <T> T mapToJavaBean(Object mapObj, Class<T> t) {
        String str = toJson(mapObj);
        if (!StringUtils.isEmpty(str)) {
            return fromJson(str, t);
        } else {
            return null;
        }
    }
}
