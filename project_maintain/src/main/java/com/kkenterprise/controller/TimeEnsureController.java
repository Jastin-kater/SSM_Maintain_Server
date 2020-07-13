package com.kkenterprise.controller;


import com.kkenterprise.utils.RSAUtils;
import com.kkenterprise.domain.beans.TandPublicKeyBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.kkenterprise.utils.RSAUtils.getPublicKey;

/**
 * 客户端确认时差工具类
 */
@Controller
@RequestMapping(path = "/time")
public class TimeEnsureController {
    /**
     * 获取公钥并返回系统时间
     */
    @RequestMapping(path = "/ensureTime")
    public @ResponseBody TandPublicKeyBean ensureTime(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //获取公钥字符串以及时间  并封装为Json
        RSAUtils.setFileName(RSAUtils.DATAKEY);
        TandPublicKeyBean tandPublicKeyBean = new TandPublicKeyBean(RSAUtils.bytesToHexString(getPublicKey().getEncoded()),String.valueOf(getSystemTime()));
        return  tandPublicKeyBean;
    }

    public long getSystemTime() throws Exception
    {
        return System.currentTimeMillis();
    }


}
