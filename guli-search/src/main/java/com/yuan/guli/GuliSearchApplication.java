package com.yuan.guli;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableRedisHttpSession
@EnableFeignClients
@EnableDiscoveryClient //服务注册和发现
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class GuliSearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(GuliSearchApplication.class, args);
    }

}
