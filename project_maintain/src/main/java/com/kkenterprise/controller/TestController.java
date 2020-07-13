package com.kkenterprise.controller;

import com.kkenterprise.service.redis.impl.VerificationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequestMapping(path = "/test")
@Controller
public class TestController {

    @Autowired
    VerificationServiceImpl verificationService;
    @RequestMapping(path = "/redistest")
    public void redistest(HttpServletResponse response) throws IOException {
        System.out.println("come in");
        verificationService.addVcodeToRedis("17854222103","666666");
        if(verificationService.hasKey("17854222103")) {
            System.out.println("hasKey(\"17854222103\")");
            System.out.println("code:"+ verificationService.getKeyValue("17854222103"));
            response.getWriter().write("ok!");
            return;
        }
        response.getWriter().write("no!");
    }

}
