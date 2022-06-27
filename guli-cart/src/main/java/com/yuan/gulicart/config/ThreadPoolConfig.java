package com.yuan.gulicart.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 线程池属性配置：绑定配置文件的方式
 * */
@ConfigurationProperties(prefix = "guli.thread")
@Component //将属性绑定到容器
@Data
public class ThreadPoolConfig {
        private  Integer coreSize;
        private  Integer maxSize;
        private  Integer keepAlivetTime;
}
