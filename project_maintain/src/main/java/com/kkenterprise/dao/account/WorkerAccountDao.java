package com.kkenterprise.dao.account;


import com.kkenterprise.domain.beans.Userbean;
import com.kkenterprise.domain.beans.Workerbean;
import org.springframework.stereotype.Repository;

/**
 * 用户操作接口
 */
@Repository
public interface WorkerAccountDao {

    /**
     * 存储工人信息
     * @param worker
     */
    public void saveWorkerInfo(Workerbean worker) throws Exception;


    /**
     * 根据手机号查询工人信息
     * @param phone
     * @return
     */
    public Workerbean findWorkerByPhone(String phone) throws Exception;




    /**
     * 获取工人头像
     */





    /**
     * 获取工人近三个月订单信息
     */

}
