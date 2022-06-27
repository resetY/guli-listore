package com.yuan.guli.auth.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 使用springmvc的配置，将请求和页面进行映射
 * 不需要写那么多空方法进行页面跳转了
 *    @RequestMapping("regist.html") //访问/ 啥的都进行index.html的跳转
 *     public String login(){
 *         return  "regist";
 *     }
 * **/
@Configuration
public class GuliWebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
            registry.addViewController("/login.html").setViewName("login");
        registry.addViewController("/regist.html").setViewName("regist");
    }
}
