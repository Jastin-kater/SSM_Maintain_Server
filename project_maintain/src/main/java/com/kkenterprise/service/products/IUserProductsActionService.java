package com.kkenterprise.service.products;

import com.alipay.api.AlipayApiException;
import com.kkenterprise.domain.beans.Productsbean;

import java.text.ParseException;
import java.util.Map;

public interface IUserProductsActionService {



    /**
     * 支付宝异步回调
     * @param conversionParams conversionParams
     * @return String
     */
    String aliNotify( Map<String, String> conversionParams) throws AlipayApiException, ParseException;

    /**
     * 根据产品的Id查询价格
     * @param productId 产品Id
     * @return
     */
    public Productsbean findPriceById(String productId);




    String createOrder(Productsbean productsbean, String num, String userid, double x, double y, String address,String remarks,String picture);
}
