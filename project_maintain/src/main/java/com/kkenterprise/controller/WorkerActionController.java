package com.kkenterprise.controller;

import com.google.gson.JsonObject;
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
@RequestMapping("/WorkerActionController")
public class WorkerActionController {
    @Autowired
    private VerificationServiceImpl verificationService;
    @RequestMapping("/getNearByOrders")
    public void getNearByOrders(@RequestBody String request, HttpServletResponse response){
        JsonObject jsonObject = jsonParse(request);
        String id = jsonObject.get("id").getAsString();
        String data = jsonObject.get("data").getAsString();
        //获取密匙和iv向量
        String aeskey = verificationService.hashGet(id, "AESkey");
        String iv = verificationService.hashGet(id, "Iv");
        //解密数据
        data = AesUtils.decrypt(data, Base64.getDecoder().decode(aeskey), Base64.getDecoder().decode(iv));
        //获取到工匠位置
        JsonObject products = jsonParse(data);
        String latitude = products.get("latitude").getAsString();
        String longitude = products.get("longitude").getAsString();
        //查询附近订单
        verificationService.findNearbyOrders(Double.parseDouble(longitude),Double.parseDouble(latitude),1);

        //返回订单列表


    }
}
