package com.kkenterprise.controller;


import com.google.gson.JsonObject;
import com.kkenterprise.service.products.impl.SeckillService;
import com.kkenterprise.service.redis.impl.VerificationServiceImpl;
import com.kkenterprise.utils.AesUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;

import java.util.Base64;

import static com.kkenterprise.controller.UserActionController.jsonParse;

@Controller
@RequestMapping("/OrderKillController")
public class OrderKillController {
    @Autowired
    private VerificationServiceImpl verificationService;
    @Autowired
    private SeckillService seckillService;
    @RequestMapping("/killOrders")
    public void killOrders(@RequestBody String request, HttpServletResponse response){
        JsonObject jsonObject = jsonParse(request);
        String id = jsonObject.get("id").getAsString();
        String data = jsonObject.get("data").getAsString();
        //获取密匙和iv向量
        String aeskey = verificationService.hashGet(id, "AESkey");
        String iv = verificationService.hashGet(id, "Iv");
        //解密数据
        data = AesUtils.decrypt(data, Base64.getDecoder().decode(aeskey), Base64.getDecoder().decode(iv));
        //获取到订单号
        JsonObject products = jsonParse(data);
        String out_trade_no = products.get("out_trade_no").getAsString();

        seckillService.orderProductMocckDiffUser(out_trade_no,Integer.parseInt(id));

    }



}
