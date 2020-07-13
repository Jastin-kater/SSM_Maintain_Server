package com.kkenterprise.service.products;
public interface ISeckillService {



    /**
     * 秒杀订单的逻辑方法
     * @param order
     */
    void orderProductMocckDiffUser(String order,Integer id);
}

