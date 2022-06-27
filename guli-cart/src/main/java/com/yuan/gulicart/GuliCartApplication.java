package com.yuan.gulicart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableRedisHttpSession
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.yuan.gulicart.feign")
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class}) //排除和数据源有关的配置
public class GuliCartApplication {

    public static void main(String[] args) {
        SpringApplication.run(GuliCartApplication.class, args);
    }

}
