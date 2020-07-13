package com.kkenterprise.service.account.impl;

import com.kkenterprise.dao.account.UserAccountDao;
import com.kkenterprise.domain.beans.Userbean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserAccountService {
    @Autowired
    private UserAccountDao userAccountDao;

    /**
     * 存储用户信息
     */
    public void saveUserInfo(Userbean user) throws Exception{
        userAccountDao.saveUserInfo(user);

    }


    /**
     * 根据手机号查询用户信息
     * @param phone
     * @return
     */
    public Userbean findUserByPhone(String phone) throws Exception{

        return userAccountDao.findUserByPhone(phone);


    }



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
