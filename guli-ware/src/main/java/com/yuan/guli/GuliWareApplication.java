package com.yuan.guli;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableRabbit
@SpringBootApplication
@MapperScan("com.yuan.guli.guliware.dao")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.yuan.guli.guliware.feign")
public class GuliWareApplication {

    public static void main(String[] args) {
        SpringApplication.run(GuliWareApplication.class, args);
    }

}
