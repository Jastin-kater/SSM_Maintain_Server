package com.kkenterprise.controller;

import com.alipay.api.AlipayApiException;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kkenterprise.domain.beans.Productsbean;
import com.kkenterprise.domain.beans.TAResultbean;
import com.kkenterprise.service.products.impl.UserProductsActionService;
import com.kkenterprise.service.redis.impl.VerificationServiceImpl;
import com.kkenterprise.utils.AesUtils;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import sun.awt.SunHints;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.kkenterprise.controller.UserActionController.jsonParse;


@Controller
@RequestMapping(value = "/userPay")
public class PayController {
    @Autowired
    private VerificationServiceImpl verificationService;

    @Autowired
    private UserProductsActionService userProductsActionService;

    @RequestMapping(value = "/createOrder")
    public void createOrder(@RequestBody String request, HttpServletResponse response) throws Exception {
        String result ="";
        JsonObject jsonObject = jsonParse(request);
        String id = jsonObject.get("id").getAsString();
        String data = jsonObject.get("data").getAsString();
        //获取密匙和iv向量
        String aeskey = verificationService.hashGet(id, "AESkey");
        String iv = verificationService.hashGet(id, "Iv");
        //解密数据
        data = AesUtils.decrypt(data, Base64.getDecoder().decode(aeskey), Base64.getDecoder().decode(iv));
//todo:验证Token

        //获取订单信息  产品号  数量
        JsonObject products = jsonParse(data);
        String productId = products.get("productId").getAsString();
        String productNum = products.get("productNum").getAsString();
        String latitude = products.get("latitude").getAsString();
        String longitude = products.get("longitude").getAsString();
        String address = products.get("address").getAsString();
        String remarks = products.get("remarks").getAsString();
        String picture = products.get("picture").getAsString();
        //查询价格并形成完整订单 存储订单
        Productsbean productsbean = userProductsActionService.findPriceById(productId);
        result = userProductsActionService.createOrder(productsbean,productNum,id,Double.parseDouble(longitude),Double.parseDouble(latitude),address,remarks,picture);
        //返回订单字符串
        if(result != null)
        {
           response.getWriter().write( AesUtils.encrypt(result,Base64.getDecoder().decode(aeskey),Base64.getDecoder().decode(iv)));


        }else
        {
            response.getWriter().write("error");

        }


    }

    /**
     * 支付宝支付成功后.异步请求该接口
     *
     * @param request
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/aliNotifyAsync", method = RequestMethod.POST)
    @ResponseBody
    public String aliNotifyAsync(HttpServletRequest request, HttpServletResponse response) throws IOException, AlipayApiException, ParseException {
        System.out.println("=支付宝异步返回支付结果开始");
        //1.从支付宝回调的request域中取值
        //获取支付宝返回的参数集合
        Map<String, String[]> aliParams = request.getParameterMap();
        //用以存放转化后的参数集合
        Map<String, String> conversionParams = new HashMap<String, String>();
        for (Iterator<String> iter = aliParams.keySet().iterator(); iter.hasNext(); ) {
            String key = iter.next();
            String[] values = aliParams.get(key);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            // 乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "UTF-8");
            conversionParams.put(key, valueStr);
        }
        System.out.println("==返回参数集合：" + conversionParams);
        String status = userProductsActionService.aliNotify(conversionParams);
        return status;
    }

    /**
     * 支付宝支付成功后.同步请求该接口
     *
     * @param request
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/aliNotifySync", method = RequestMethod.POST)
    public void aliNotifySync(HttpServletRequest request, HttpServletResponse response) throws IOException, AlipayApiException {
        System.out.println("=支付宝异步返回支付结果开始");

    }

    /**
     * app收到同步通知后回调确认订单
     * @param request
     * @param response
     * @throws IOException
     * @throws AlipayApiException
     */
    @RequestMapping(value = "/feedbackcheck", method = RequestMethod.POST)
    public void feedbackcheck(String request, HttpServletResponse response) throws IOException{
        //获取订单号
        String result ="";
        JsonObject jsonObject = jsonParse(request);
        String id = jsonObject.get("id").getAsString();
        String data = jsonObject.get("data").getAsString();
        //获取密匙和iv向量
        String aeskey = verificationService.hashGet(id, "AESkey");
        String iv = verificationService.hashGet(id, "Iv");
        //解密数据
        data = AesUtils.decrypt(data, Base64.getDecoder().decode(aeskey), Base64.getDecoder().decode(iv));
//todo:验证Token

        //获取数据键值对
        JsonObject datajsonOb = jsonParse(data);
        //查询支付状态
        result = userProductsActionService.checkAlipay(datajsonOb.get("out_trade_no").getAsString());
        //返回结果
        result = "{\"out_trade_no\":" + result + "}";
        response.getWriter().write(result);
    }





}