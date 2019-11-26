package com.basic.support.commons.business.redis;


import com.google.common.base.Joiner;

/**
 * redisKey设计
 */
public class RedisKeyUtil {

    private static Joiner joiner = Joiner.on(":");
    /**
     * redis的key
     * 形式为：
     * 表名:主键名:主键值:列名
     *
     * @param tableName 表名
     * @param majorKey 主键名
     * @param majorKeyValue 主键值
     * @param column 列名
     * @return
     */
    public static String getKeyWithColumn(String tableName,String majorKey,String majorKeyValue,String column){
        return joiner.join(tableName, majorKey,majorKeyValue,column);
    }
    /**
     * redis的key
     * 形式为：
     * 表名:主键名:主键值
     *
     * @param tableName 表名
     * @param majorKey 主键名
     * @param majorKeyValue 主键值
     * @return
     */
    public static String getKey(String tableName,String majorKey,String majorKeyValue){
        return joiner.join(tableName, majorKey,majorKeyValue);
    }


}
