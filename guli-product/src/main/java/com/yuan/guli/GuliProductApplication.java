package com.yuan.guli;

import com.yuan.guli.guliproduct.service.BrandService;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.stereotype.Service;

@EnableFeignClients(basePackages = "com.yuan.guli.guliproduct.feign") //扫描远程服务的包
@MapperScan("com.yuan.guli.guliproduct.dao")
@SpringBootApplication
@EnableDiscoveryClient
@EnableCaching
@EnableRedisHttpSession
public class GuliProductApplication {

//    @Autowired
//    BrandService brandService;

    public static void main(String[] args) {
        SpringApplication.run(GuliProductApplication.class, args);
    }

}
