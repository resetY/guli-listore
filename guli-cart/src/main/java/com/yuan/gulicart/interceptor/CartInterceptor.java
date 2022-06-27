package com.yuan.gulicart.interceptor;

import com.yuan.common.to.MemberRespVo;
import com.yuan.gulicart.vo.UserInfoTo;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 购物车的拦截器，在执行目标方法前，判断用户状态为临时用户还是登录用户
 *      然后封装传递给controllr请求
 * */
@Component
public class CartInterceptor implements HandlerInterceptor {


    //用于数据共享，将数据传送给控制岑
    public static ThreadLocal<UserInfoTo> threadLocal = new ThreadLocal<>();


    @Override  //目标方法执行前拦截
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {

        UserInfoTo userInfoTo = new UserInfoTo();
        HttpSession session = request.getSession();
        MemberRespVo user = (MemberRespVo) session.getAttribute("loginUser");
        if(user!=null){
            userInfoTo.setUserId(user.getId());
        }
        Cookie[] cookies = request.getCookies();
        if(cookies!=null && cookies.length>0){
            for (Cookie cookie : cookies) {
                //user-key:
                String cname = cookie.getName();
                    if(cname.equals("user-key")){
                        userInfoTo.setUserKey(cookie.getValue());
                        userInfoTo.setTempUser(true);
                    }
            }
        }
        //目标方法执行前进行信息放入：
        threadLocal.set(userInfoTo);
       return  true;
    }

    @Override  /**
        业务执行后的方法
     */
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
       //临时用户的key，用cookie进行存储
        UserInfoTo userInfoTo = threadLocal.get();
      if(!userInfoTo.isTempUser()){ //如果没有临时用户信息(false的情况)，才进行添加
          Cookie cookie = new Cookie("user-key",userInfoTo.getUserKey());
          cookie.setDomain("guli.com");
          cookie.setMaxAge(60*60*24); //一个月后cookie过期
          response.addCookie(cookie);
      }

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
