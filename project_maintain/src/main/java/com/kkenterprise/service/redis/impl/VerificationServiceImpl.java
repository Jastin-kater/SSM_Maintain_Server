package com.kkenterprise.service.redis.impl;

import com.kkenterprise.dao.redis.VerificationDao;
import com.kkenterprise.service.redis.IVerificationServive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class VerificationServiceImpl implements IVerificationServive {
    @Autowired
    private VerificationDao verificationDao;
    /**
     * 以手机号为key  将验证码放入redis
     * @param phoneN
     * @param code
     * @return
     */
    public boolean addVcodeToRedis(String phoneN,String code)
    {
       return verificationDao.stadd(phoneN,code,5, TimeUnit.MINUTES);
    }


    /**
     * 设置key过期时间 5分钟
     * @param key
     * @param time
     * @return
     */
    public boolean setKeyExpiretime(String key,long time){

        return false;
    }

    /**
     * 查询key是否存在
     * @param key
     * @return
     */
    public boolean hasKey(String key){

        return verificationDao.stHasKey(key);
    }

    /**
     * 获取key的值
     * @param key
     * @return
     */
    public String getKeyValue(String key){

        return verificationDao.stget(key);
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
        verificationDao.hashSet(hashName,token,aeskey,iv,phonenum);

    }


    /**
     * 获取hash中的context的内容
     * @param hashName  手机号
     * @param contextName 内容名字
     * @return
     */
    public String hashGet(String hashName,String contextName)
    {
        return  verificationDao.hashGet(hashName,contextName);
    }

    /**
     * 刷新hash的过期时间
     * @param hashName
     */
    public void updateExpire(String hashName)
    {
        verificationDao.updateExpire(hashName);
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
        return verificationDao.addmember(orderId, x, y);

    }


    /**
     *
     * @param x 中心经度
     * @param y 中心纬度
     * @param radis 查找半径
     * @return
     */
    public GeoResults<RedisGeoCommands.GeoLocation<String>> findNearbyOrders(double x, double y, double radis){

        return verificationDao.findNearbyOrders(x, y, radis);

    }

    /**
     *
     * @param orderId 要删除的订单号
     * @return
     */
    public long deleteMember(String orderId){
        return verificationDao.deleteMember(orderId);

    }


}
