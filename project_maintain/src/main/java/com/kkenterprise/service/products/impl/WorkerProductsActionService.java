package com.kkenterprise.service.products.impl;


import com.kkenterprise.dao.products.ProductsDao;
import com.kkenterprise.domain.beans.Ordersbean;
import com.kkenterprise.domain.beans.WorkerOrderbean;
import com.kkenterprise.service.products.IWorkerProductsActionService;
import com.kkenterprise.utils.MoneyCountUtil;
import com.kkenterprise.utils.OrderIdCreateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class WorkerProductsActionService implements IWorkerProductsActionService {
    @Autowired
    ProductsDao productsDao;

    @Override
    public WorkerOrderbean createWorkerOrder(Ordersbean order, Integer workerId) {
        String orderNo = OrderIdCreateUtils.getOrderIdByTime();//这行代码是生成一个商户的订单号..根据你们自己的业务需求去生成就可以了..

        WorkerOrderbean ordersbean = new WorkerOrderbean();
        ordersbean.setOrder_num(orderNo);
        ordersbean.setBuy_counts(order.getBuy_counts());
        ordersbean.setOrder_amount(order.getOrder_amount());
        ordersbean.setOrder_status("0");
        ordersbean.setProduct_id(order.getProduct_id());
        ordersbean.setCreate_time(new Date());
        ordersbean.setUserId(order.getUserId());
        ordersbean.setWorkerId(workerId);
        ordersbean.setLongitude(order.getLongitude());
        ordersbean.setLatitude(order.getLatitude());
        ordersbean.setAddress(order.getAddress());
        ordersbean.setRemarks(order.getRemarks());
        ordersbean.setPicture(order.getPicture());


        productsDao.createWorkerOrder(ordersbean);
        return ordersbean;
    }


}
