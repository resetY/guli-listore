package com.yuan.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * 跨域请求的配置
 * **/
@Configuration
public class GuliCorsConfiguration {

    @Bean //加入到spring容器进行管理
  public CorsWebFilter corsWebFilter(){
    // CorsConfigurationSource的实现类：
      UrlBasedCorsConfigurationSource corsConfigurationSource = new UrlBasedCorsConfigurationSource();
      //1.配置跨域
      CorsConfiguration conf = new CorsConfiguration();
      conf.addAllowedHeader("*");  //请求头配置
      conf.addAllowedMethod("*");   //允许任意请求方式(get post  put delte)
      conf.addAllowedOrigin("*");  //任意请求来源
        conf.setAllowCredentials(true); //是否允许cookie进行跨域
      //2.配置跨域的路径：/** 为任意路径，然后添加跨域的配置
      corsConfigurationSource.registerCorsConfiguration("/**",conf);

      return new CorsWebFilter(corsConfigurationSource);
  }

}
