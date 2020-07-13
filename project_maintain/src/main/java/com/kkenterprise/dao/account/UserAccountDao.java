package com.kkenterprise.dao.account;


import com.kkenterprise.domain.beans.Userbean;
import com.kkenterprise.domain.beans.Workerbean;
import org.springframework.stereotype.Repository;

/**
 * 用户操作接口
 */
@Repository
public interface UserAccountDao {
    /**
     * 存储用户信息
     */
    public void saveUserInfo(Userbean user) throws Exception;


    /**
     * 根据手机号查询用户信息
      * @param phone
     * @return
     */
    public Userbean findUserByPhone(String phone) throws Exception;



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
