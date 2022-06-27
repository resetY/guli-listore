//package com.yuan.guli.gulimember.config;
//
//import com.yuan.guli.gulimember.interceptor.LoginUserInterceptor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class MemberWebConfig implements WebMvcConfigurer {
//
//    @Autowired
//    LoginUserInterceptor loginUserInterceptor;
//    //添加拦截器
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(loginUserInterceptor).addPathPatterns("/**");
//    }
//}
