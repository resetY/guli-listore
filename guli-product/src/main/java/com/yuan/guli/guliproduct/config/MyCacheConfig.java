package com.yuan.guli.guliproduct.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@EnableCaching
@Configuration
//开启属性配置的绑定功能：使用后还需要导入配置
@EnableConfigurationProperties(CacheProperties.class)
public class MyCacheConfig {

    //1.导入配置文件方式1：
    @Autowired
  CacheProperties cacheProperties;

    @Bean
        RedisCacheConfiguration redisCacheConfiguration(){
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig(); //先设定默认配置
      //  config = config.entryTtl();

        //修改key序列化类型
        config = config.serializeKeysWith
                (RedisSerializationContext.SerializationPair.fromSerializer
                        (new StringRedisSerializer()));
        //值序列化类型：json
       config = config.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer
                (new GenericJackson2JsonRedisSerializer()));


        //将配置文件的所有配置也生效：
       //设置销毁时间：
        Duration timeToLive = cacheProperties.getRedis().getTimeToLive();
        //获取前缀
        String keyPrefix = cacheProperties.getRedis().getKeyPrefix();
        boolean nullValues = cacheProperties.getRedis().isCacheNullValues();
        config = config.entryTtl(timeToLive);
        config = config.prefixKeysWith(keyPrefix);
        if(!nullValues){
            config = config.disableCachingNullValues();
        }
        return config;
    }

}
