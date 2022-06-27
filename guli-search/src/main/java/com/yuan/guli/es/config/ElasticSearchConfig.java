package com.yuan.guli.es.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置es,给容器中注入RestHign***  用于操纵9200端口
 * */
@Configuration
public class ElasticSearchConfig {

 public static RequestOptions COMMOM_OPTIONS;
    static{
        RequestOptions.Builder builder =RequestOptions.DEFAULT.toBuilder();
        COMMOM_OPTIONS =  builder.build();
    }


    @Bean
    public RestHighLevelClient esRestClient(){

        RestClientBuilder builder = null;
        builder = RestClient.builder(
                new HttpHost("192.168.56.12", 9200, "http"));
        RestHighLevelClient client = new RestHighLevelClient(builder);
        return client;
    }
}
