package com.yuan.guli.auth.controller;

import com.alibaba.fastjson.TypeReference;
import com.yuan.common.to.MemberRespVo;
import com.yuan.common.utils.R;
import com.yuan.guli.auth.feign.MemberFeignService;
import com.yuan.guli.auth.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
public class LoginController {


    @RequestMapping("/login.html")
    public String loginPage(HttpSession session){
        if(session.getAttribute("loginUser")== null){ //没登录
            return "login";
        }else{
            return "redirect:http://guli.com/";
        }

    }

//    @RequestMapping({"/","login.html"}) //访问/ 啥的都进行index.html的跳转
//    public String login(){
//        return  "login";
//    }

    /**
     * 进行登录操作
     * */


    @Autowired
    MemberFeignService memberFeignService;


    @RequestMapping("/login")
    public String login(LoginVo loginVo, RedirectAttributes redirectAttributes,
                   HttpSession session){
            //调用远程服务，查询数据库中是否匹配
        R r = memberFeignService.login(loginVo);
        System.out.println("登录状态："+r.getCode());
        if(r.getCode()==0){ //登录成功
            MemberRespVo data = r.getData("data", new TypeReference<MemberRespVo>() {
            });
            session.setAttribute("loginUser",data); //保存用户名到session
            return "redirect:http://guli.com/";
        }else{
            Map<String,String> errors = new HashMap<>();
            errors.put("msg",r.getData("msg",new TypeReference<String>(){}));
           redirectAttributes.addFlashAttribute("errors",errors);
            return "redirect:http://auth.guli.com/login.html";
        }
    }


}
