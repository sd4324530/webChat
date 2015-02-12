package com.github.sd4324530.webChat.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.Map;

import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * @ClassName: JSONUtil
 * @Description: json操作工具类
 * @author peiyu
 */
public abstract class JSONUtil {
	
    /**
     * 默认json格式化方式
     */
    public static final SerializerFeature[] DEFAULT_FORMAT = { SerializerFeature.WriteDateUseDateFormat, SerializerFeature.WriteEnumUsingToString,
        SerializerFeature.WriteNonStringKeyAsString, SerializerFeature.QuoteFieldNames, SerializerFeature.SkipTransientField,
        SerializerFeature.SortField, SerializerFeature.PrettyFormat };

    /**
     * @Title: getStringFromJSONObject
     * @Description: 从json获取指定key的字符串
     * @param json json字符串
     * @param key 字符串的key
     * @return 指定key的值
     */
    public static Object getStringFromJSONObject(final String json, final String key) {
    	requireNonNull(json, "json is null");
        return JSON.parseObject(json).getString(key);
    }

    /**
     * @Title: getJSONFromString
     * @Description: 将字符串转换成JSON字符串
     * @param jsonString json字符串
     * @return 转换成的json对象
     */
    public static JSONObject getJSONFromString(final String jsonString) {
        if (isBlank(jsonString)) {
            return new JSONObject();
        }
        return JSON.parseObject(jsonString);
    }

    /**
     * @Title: toBean
     * @Description: 将json字符串，转换成指定java bean
     * @param jsonStr json串对象
     * @param beanClass 指定的bean
     * @param <T> 任意bean的类型
     * @return 转换后的java bean对象
     */
    public static <T> T toBean(String jsonStr, Class<T> beanClass) {
    	requireNonNull(jsonStr, "jsonStr is null");
    	
    	JSONObject jo = JSON.parseObject(jsonStr);
        jo.put(JSON.DEFAULT_TYPE_KEY, beanClass.getName());
        return JSON.parseObject(jo.toJSONString(), beanClass);
//       return Json.fromJson(beanClass, jsonStr);
    }
    
    /**
     * @Title: toJson
     * @param obj 需要转换的java bean
     * @return 对应的json字符串
     */
    public static <T> String toJson(T obj) {
    	requireNonNull(obj, "obj is null");
    	
        return JSON.toJSONString(obj, DEFAULT_FORMAT);
//        return Json.toJson(obj);
    }
    
    /**
     * 通过Map生成一个json字符串
     * @param map
     * @return
     */
    public static String toJson(Map<String, Object> map) {
    	requireNonNull(map, "map is null");
    	
        return JSON.toJSONString(map, DEFAULT_FORMAT);
//        return Json.toJson(map);
    }
    
    /**
     * 美化传入的json,使得该json字符串容易查看
     * @param jsonString
     * @return
     */
    public static String prettyFormatJson(String jsonString) {
    	requireNonNull(jsonString, "jsonString is null");
        return JSON.toJSONString(getJSONFromString(jsonString), true);
    }
    
    /**
     * 将传入的json字符串转换成Map
     * @param jsonString
     * @return
     */
    public static Map<String, Object> toMap(String jsonString) {
    	requireNonNull(jsonString, "jsonString is null");
    	return getJSONFromString(jsonString);
    }
}
