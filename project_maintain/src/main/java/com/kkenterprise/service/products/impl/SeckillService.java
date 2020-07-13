package com.kkenterprise.service.products.impl;

import com.kkenterprise.dao.products.ProductsDao;
import com.kkenterprise.domain.beans.Ordersbean;
import com.kkenterprise.domain.beans.WorkerOrderbean;
import com.kkenterprise.service.products.ISeckillService;
import com.kkenterprise.service.redis.impl.VerificationServiceImpl;
import com.kkenterprise.utils.RedisLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SeckillService implements ISeckillService {
    private static int  TIMEOUT = 3000;
    @Autowired
    private RedisLock redisLock;
    @Autowired
    private WorkerProductsActionService workerProductsActionService;
    @Autowired
    private ProductsDao productsDao;
    @Autowired
    private VerificationServiceImpl verificationService;

    private Logger log = LoggerFactory.getLogger(SeckillService.class);
    @Override
    public void orderProductMocckDiffUser(String order,Integer id) {

        //加锁
        long time = System.currentTimeMillis() + TIMEOUT;
        if(!redisLock.lock(order,String.valueOf(time))){
            throw new RuntimeException("很抱歉，人太多了，换个姿势再试试~~");
        }
        //1.工人端生成订单
            //1.1查询数据库  获取用户订单
        WorkerOrderbean workeroder =  workerProductsActionService.createWorkerOrder(productsDao.findOrderByoutTradeNo(order),id);
        //2. 做出判断  若是成功则删除订单池的订单并更新数据库库
            //2.1更新数据库信息
        Ordersbean ordersbean = productsDao.findOrderByoutTradeNo(order);
        ordersbean.setWorkerId(id);
        productsDao.updateOrder(ordersbean);
            //2.2删除订单池订单
        if(verificationService.deleteMember(order) != 0)
        {
            log.info("SeckillService","删除订单池订单成功");

        }else
        {
            log.error("SeckillService","删除订单池订单失败");
        }

        redisLock.unlock(order,String.valueOf(time));
        }

    }


