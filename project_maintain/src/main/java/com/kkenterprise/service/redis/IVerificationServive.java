package com.kkenterprise.service.redis;

public interface IVerificationServive {

    /**
     * 以手机号为key  将验证码放入redis
     * @param phoneN
     * @param code
     * @return
     */
    public boolean addVcodeToRedis(String phoneN,String code);


    /**
     * 设置key过期时间 5分钟
     * @param key
     * @param time
     * @return
     */
    public boolean setKeyExpiretime(String key,long time);

    /**
     * 查询key是否存在
     * @param key
     * @return
     */
    public boolean hasKey(String key);

    /**
     * 获取key的值
     * @param key
     * @return
     */
    public String getKeyValue(String key);




}
