package com.kkenterprise.dao.redis;

import com.kkenterprise.domain.beans.Ordersbean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Repository
public class VerificationDao {
    @Autowired
    private RedisTemplate redisTemplate;
/****************String操作*****************/
    /**
     * 加入string并且设置过期时间
     * @param key
     * @param value
     * @param extime
     * @param tUnit
     * @return
     */
    public boolean stadd(String key,String value,long extime,TimeUnit tUnit)
    {
        redisTemplate.opsForValue().set(key,value,extime,tUnit);

        System.out.println("放置数据值到hash");
        return redisTemplate.hasKey(key);

    }

    /**
     * 查询key是否存在
     * @param key
     * @return
     */
    public boolean stHasKey(String key)
    {
        return redisTemplate.hasKey(key);
    }


    /**
     * 获取key值
     * @param key
     * @return
     */
    public String stget(String key)
    {
       return (String) redisTemplate.opsForValue().get(key);

    }

/*********************hash**************************/
    /**
     * 存储Token和AES密匙以及Iv向量
     * @param hashName
     * @param token
     * @param aeskey
     * @param iv
     */
    public void hashSet(String hashName,String token,String aeskey,String iv,String phonenum)
{
    String hashkey = hashName+"hash";
    Map<String,Object> map = new HashMap();
    map.put("Token",token);
    map.put("AESkey",aeskey);
    map.put("Iv",iv);
    map.put("Phonenum",phonenum);
    redisTemplate.opsForHash().putAll(hashkey,map);
    //设置过期时间3天
    redisTemplate.opsForHash().getOperations().expire(hashName,3,TimeUnit.DAYS);

}


    /**
     * 获取hash中的context的内容
     * @param hashName  手机号
     * @param contextName 内容名字
     * @return
     */
    public String hashGet(String hashName,String contextName)
{
return (String)redisTemplate.opsForHash().get(hashName,contextName);
}

    /**
     * 刷新hash的过期时间
     * @param hashName
     */
    public void updateExpire(String hashName)
{
    if(redisTemplate.opsForHash().getOperations().getExpire(hashName,TimeUnit.HOURS) >= 50)
    {
        //刷新过期时间
        redisTemplate.opsForHash().getOperations().expire(hashName,3,TimeUnit.DAYS);

    }
}


/************************georedis操作************************/
    /**
     *
     * @param orderId 订单号
     * @param x 经度
     * @param y 纬度
     * @return
     */
    public long addmember(String orderId,double x,double y)
{
    return redisTemplate.opsForGeo().add("ordersGeo",new Point(x,y),orderId);

}


    /**
     *
     * @param x 中心经度
     * @param y 中心纬度
     * @param radis 查找半径
     * @return
     */
    public GeoResults<RedisGeoCommands.GeoLocation<String>> findNearbyOrders(double x, double y,double radis){
    Point center = new Point(x,y);
    Distance distance = new Distance(radis,Metrics.KILOMETERS);
    Circle circle = new Circle(center,distance);
    //limit 10表示只截取10个订单 includeCoordinates表示返回经纬度 sortAscending由近到远排列
    RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs().includeCoordinates().sortAscending().limit(10);
    GeoResults<RedisGeoCommands.GeoLocation<String>> results = redisTemplate.opsForGeo().radius("ordersGeo",circle,args);
    return results;

}

    /**
     *
     * @param orderId 要删除的订单号
     * @return
     */
    public long deleteMember(String orderId){
       return redisTemplate.opsForGeo().remove("ordersGeo", orderId);

}

}
