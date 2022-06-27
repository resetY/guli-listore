package com.yuan.guli;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;


/**
 * 1.想要远程调用别的服务
 *      ① 引入 openFeign
 *      ② 编写一个接口，告诉SpringCloud这个接口需要调用远程服务
 * **/

@EnableRedisHttpSession //开启session共享
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.yuan.guli.gulimember.feign")
@MapperScan("com.yuan.guli.gulimember.dao")
public class GuliMemberApplication {

    public static void main(String[] args) {
        SpringApplication.run(GuliMemberApplication.class, args);
    }

}
