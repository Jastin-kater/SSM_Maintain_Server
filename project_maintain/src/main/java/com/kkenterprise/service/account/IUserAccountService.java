package com.kkenterprise.service.account;

import com.kkenterprise.domain.beans.Userbean;

public interface IUserAccountService {


    /**
     * 存储用户信息
     */
    public void saveUserInfo(Userbean user);


    /**
     * 根据手机号查询用户信息
     * @param phone
     * @return
     */
    public Userbean findUserByPhone(String phone);



    /**
     * 获取用户头像
     */





    /**
     * 获取用户地址
     */


    /**
     * 获取用户近三个月订单信息
     */


}
