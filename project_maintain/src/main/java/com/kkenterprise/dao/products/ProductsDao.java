package com.kkenterprise.dao.products;


import com.kkenterprise.domain.beans.Ordersbean;
import com.kkenterprise.domain.beans.Productsbean;
import com.kkenterprise.domain.beans.WorkerOrderbean;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductsDao {

    /*****************************产品信息********************************/
    /**
     * 根据产品的Id查询价格
     * @param productId 产品Id
     */
    public Productsbean findPriceById(String productId);

    /****************************用户订单处理*****************************/
    /**
     * 创建用户订单
     * @param ordersbean
     */
    public void createOrder(Ordersbean ordersbean);

    /**
     * 更新订单数据
     * @param ordersbean
     */
    public void updateOrder(Ordersbean ordersbean);

    /**
     * 根据单号查询订单信息
     * @param outTradeNo 订单号
     * @return
     */

    public Ordersbean findOrderByoutTradeNo(String outTradeNo);

    /**************************工人订单处理*****************************/
    /**
     * 创建工人订单
     * @param workerOrderbean
     */
    public void createWorkerOrder(WorkerOrderbean workerOrderbean);
    /**
     * 更新工人订单数据
     * @param ordersbean
     */
    public void updateWorkerOrder(Ordersbean ordersbean);

    /**
     * 根据工人单号查询订单信息
     * @param outTradeNo 订单号
     * @return
     */
    public Ordersbean findWorkerOrderByoutTradeNo(String outTradeNo);

}
