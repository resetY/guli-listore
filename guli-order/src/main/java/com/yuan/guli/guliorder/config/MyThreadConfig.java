package com.yuan.guli.guliorder.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 自定义线程池配置
 * */
//@EnableConfigurationProperties(ThreadPoolConfig.class)使用这个可以指定使用哪个配置，那个配置类不需要Compent
@Configuration
public class MyThreadConfig {
@Bean
public ThreadPoolExecutor threadPoolExecutor(ThreadPoolConfig pool) {
        //主线程：10 最大线程数量200 过期时间：30s 工厂：默认工厂  拒绝策略：新任务进入直接进行抛弃的策略
       return  new ThreadPoolExecutor(pool.getCoreSize(),
               pool.getMaxSize(), pool.getKeepAlivetTime(), TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(300),
                Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
}

}
