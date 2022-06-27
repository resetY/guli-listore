package com.yuan.guli.auth.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.yuan.common.to.MemberRespVo;
import com.yuan.common.to.SocailUser;
import com.yuan.common.utils.HttpUtils;
import com.yuan.common.utils.R;
import com.yuan.guli.auth.feign.MemberFeignService;


import org.apache.commons.collections.map.HashedMap;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * 处理社交登录请求
 *
 *          {
 *     "access_token": "2.00rAr4vGeAPv3D7f6ca1da4cwRNvCE",
 *     "remind_in": "157679999",
 *     "expires_in": 157679999,
 *     "uid": "6344733473",
 *     "isRealName": "true"
 * }
 *
 * 将响应体生成为javabean对象
 * */

@Controller
public class Oauth2Controller {

    @Autowired
    MemberFeignService memberFeignService;
    @RequestMapping("/oauth2/weibo/success")
    public String weibo(@RequestParam("code") String code, HttpSession session, HttpServletResponse hsresp) throws Exception {

        Map<String,String>header = new HashMap<>();
        Map<String,String>query = new HashMap<>();
        /**
         * host:主网址
         * path：请求接口路径
         * method：请求方式
         * headers:请求头
         * querys：查询参数
         * body：请求体
         * */
        Map<String,String> map = new HashedMap();

        map.put("client_id","3087120284");
        map.put("client_secret","a57076c592729f30db6447ecb143f29d");
        map.put("grant_type","authorization_code");
        map.put("redirect_uri","http://auth.guli.com/oauth2/weibo/success");
        map.put("code",code);
        //1.根据code换取accesstoken
        HttpResponse response = HttpUtils.doPost("https://api.weibo.com", "/oauth2/access_token",
                "post", header, query, map);

        //2.处理
        int status = response.getStatusLine().getStatusCode();
        if(status==200){ //响应成功
            String resp = EntityUtils.toString(response.getEntity()); //将响应内容实体类转化为json格式数据
            SocailUser socailUser = JSON.parseObject(resp, SocailUser.class);//转化为对应对象
                socailUser.getAccess_token();
                //知道当前是哪个社交用户登录
            //1.当前用户第一次进入网站，自动注册进网站(为当前社交用户生成一个商城会员账号，该社交用户关联此账号)
            //登录或者注册这个社交用户
            R r = memberFeignService.oauthLogin(socailUser);
            if(r.getCode()==0){
//                    return "redirect:http//guli.com";
                MemberRespVo data = r.getData("data", new TypeReference<MemberRespVo>() {
                });
                System.out.println("登录成功，用户数据："+data);

//               配合cookie进行发卡来共享session，SpringSession可以简化这些操作
//                session.setAttribute("loginUser",data);
//                new Cookie("JSESSIONID","data");
                session.setAttribute("loginUser",data);
                return  "redirect:http://guli.com/";
            }else{
                return "redirect:http://auth.guli.com/login.html";
            }
        }else{ //重新登录
            return "redirect:http://auth.guli.com/login.html";
        }

        }


}
