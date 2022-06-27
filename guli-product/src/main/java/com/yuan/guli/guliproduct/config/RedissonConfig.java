package com.yuan.guli.guliproduct.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.spring.cache.CacheConfig;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class RedissonConfig {


/**
 *   RedissonClient：redisson客户端
    @return redisson实例
 * **/
//多节点配置
    @Bean(destroyMethod="shutdown")
   public  RedissonClient redisson() throws IOException {
        Config config = new Config();
        //单节点配置:需要加上 redis://  或者  rediss://连接协议名
        config.useSingleServer().setAddress("redis://192.168.56.12:6379");
            //多节点(集群)配置：
//        config.useClusterServers()
//                .addNodeAddress("127.0.0.1:7004", "127.0.0.1:7001");
        return Redisson.create(config); //返回redisson实例
    }

}
