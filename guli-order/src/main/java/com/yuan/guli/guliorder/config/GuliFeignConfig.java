package com.yuan.guli.guliorder.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Configuration
public class GuliFeignConfig { //请求拦截器的构建

    @Bean
public RequestInterceptor requestInterceptor(){
        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                //1.可以使用方法获取刚进来的请求的数据
                ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();//获取当前请求的属性
                if(requestAttributes!=null){
                   HttpServletRequest req = requestAttributes.getRequest(); //获取到当前请求

                   if(req!=null){
                       //同步请求头数据信息Cookie
                       String old_cookie = req.getHeader("Cookie");
                       //给新请求同步老请求的cookie数据
                       template.header("Cookie",old_cookie);
                   }
               }
               }
        };
        return requestInterceptor;
}

}
