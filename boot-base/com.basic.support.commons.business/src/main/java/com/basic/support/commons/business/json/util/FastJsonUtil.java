package com.basic.support.commons.business.json.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

/**
 * FastJson 工具类
 * change：
 * 增加类属性赋值功能
 * @version: 1.1.1
 */
public class FastJsonUtil {


    public static JSONObject toJSONObject(String str){
        return JSONObject.parseObject(str);
    }

    public static <T> List<T> toBeanList(JSONArray array, Class<T> clazz){
        List<T> result =  FastJsonUtil.toBeanList(array.toJSONString(),clazz);
        return result;
    }

    public static <T> List<T> toBeanList(String listStr, Class<T> clazz){
        List<T> result =  JSONObject.parseArray(listStr,clazz);
        return result;
    }

    public static Map<String,Object> toMap(String str){
        return  (Map<String,Object>) JSONObject.parseObject(str);
    }

    public static <T> Map<String,T> toMap(JSONObject obj, Class<T> clazz){
        return  (Map<String,T>) obj;
    }

    public static JSONObject map2JSON(Map<String,Object> map){
        return new  JSONObject(map);
    }

    public static String map2Str(Map<String,Object> map){
        return JSON.toJSONString(map);
    }

    public static JSONArray listMap2JsonArr(List<Map<Object, Object>> maps){
        return JSONArray.parseArray(JSON.toJSONString(maps));
    }

    public static <T> JSONObject bean2Json(T bean){
        return  JSONObject.parseObject(JSON.toJSONString(bean));
    }

    public static <T> String bean2JsonStr(T bean){
        return  JSON.toJSONString(bean);
    }

    public static <T> T json2Bean(JSONObject obj, Class<T> clazz){
        return JSON.parseObject(obj.toJSONString(), clazz);
    }

    public static <D, T> T copyProperties(D d, Class<T> clazz){
        Assert.notNull(d, "First param must not be null");
        Assert.notNull(clazz, "Result clazz must not be null");
        String copyStr = bean2JsonStr(d);
        T t = JSON.parseObject(copyStr, clazz);
        return t;
    }

    public static <D, T> List<T> copyBeanList(List<D> d, Class<T> clazz){
        Assert.notNull(d, "First param must not be null");
        Assert.notNull(clazz, "Result clazz must not be null");
        List<T> t = toBeanList(JSON.toJSONString(d), clazz);
        return t;
    }

}
