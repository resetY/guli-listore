package com.yuan.guli.auth.controller;

import com.yuan.common.utils.R;
import com.yuan.guli.auth.component.Sms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.Random;

//验证码服务
@RestController
@RequestMapping("/sms")
public class ToolsController {

    /**
     * //这个服务接口是由其他服务来调用的:由认证服务调用
     * **/
    @Autowired
    Sms sms;
    @RequestMapping("/sendCode")
        public R tools(@RequestParam("code") String code, @RequestParam("phone") String phone){
            sms.UseTools(code,phone);
            return  R.ok();
    }

}
