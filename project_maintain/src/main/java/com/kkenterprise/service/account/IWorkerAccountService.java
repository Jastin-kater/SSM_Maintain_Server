package com.kkenterprise.service.account;

import com.kkenterprise.domain.beans.Workerbean;

public interface IWorkerAccountService {


    /**
     * 存储工人信息
     * @param worker
     */
    public void saveWorkerInfo(Workerbean worker);


    /**
     * 根据手机号查询工人信息
     * @param phone
     * @return
     */
    public Workerbean findWorkerByPhone(String phone);




    /**
     * 获取工人头像
     */





    /**
     * 获取工人近三个月订单信息
     */

}
