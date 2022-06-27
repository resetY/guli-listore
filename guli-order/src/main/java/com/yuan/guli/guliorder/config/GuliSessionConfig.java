package com.yuan.guli.guliorder.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

@Configuration
public class GuliSessionConfig {

    /**
     * 配置session域共享
     * */
    @Bean
    public CookieSerializer cookieSerializer(){
       DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
        cookieSerializer.setDomainName("guli.com");
        cookieSerializer.setCookieName("GuliSession");

        return  cookieSerializer;
    }

    /**
     * 配置序列化机制
     * */
    @Bean
    public RedisSerializer<Object>serializer(){
        return  new GenericJackson2JsonRedisSerializer();
    }

}
