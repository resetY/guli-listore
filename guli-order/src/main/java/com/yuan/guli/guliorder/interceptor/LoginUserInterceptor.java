package com.yuan.guli.guliorder.interceptor;


import com.yuan.common.to.MemberRespVo;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 拦截器：拦截进入订单页面的
 * **/
@Component
public class LoginUserInterceptor implements HandlerInterceptor {

    //用于数据共享
    public static ThreadLocal<MemberRespVo>loginUser = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // /guliorder/order/getOrder/{orderSn}
        String uri = request.getRequestURI();
        //路径匹配器：如果是下边两个路径，则直接放行
        boolean match = new AntPathMatcher().match("/guliorder/order/getOrder/**", uri);
        boolean match1 = new AntPathMatcher().match("/payed/notify", uri);
        if(match || match1){
            return  true;
        }


        Object user = request.getSession().getAttribute("loginUser");
   if(user == null){
       //进行重定向
       response.sendRedirect("http://auth.guli.com/login.html");
       return false;
   }
        MemberRespVo memberRespVo = (MemberRespVo)user;
        if(memberRespVo != null){ //登录
            loginUser.set(memberRespVo);
            return true;
        }else{ //没有登录
            //进行重定向
            response.sendRedirect("http://auth.guli.com/login.html");
            return false;
        }


    }
}
