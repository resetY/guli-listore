package com.yuan;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 网关的配置
 * 1. 开启服务的注册和发现：@EnableDiscoveryClient
 * 2. 配置nacos的注册中心地址
 * 3.配置nacos配置中心
 * **/

@EnableDiscoveryClient
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class}) //排除和数据源有关的配置
public class GuliGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(GuliGatewayApplication.class, args);
    }
}
