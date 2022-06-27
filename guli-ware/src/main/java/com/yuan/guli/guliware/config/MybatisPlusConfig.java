package com.yuan.guli.guliware.config;


import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 配置Mybatis的分页插件
 * **/
@Configuration
@EnableTransactionManagement  //开启事务
@MapperScan("com.yuan.guli.guliware.dao")
public class MybatisPlusConfig{

        /**
         * 新的分页插件,一缓和二缓遵循mybatis的规则,需要设置 MybatisConfiguration#useDeprecatedExecutor = false 避免缓存出现问题(该属性会在旧插件移除后一同移除)
         */
        @Bean
        public MybatisPlusInterceptor mybatisPlusInterceptor() {
            MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
            PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();


            paginationInnerInterceptor.setMaxLimit(1000L);
            paginationInnerInterceptor.setOverflow(true); //如果搜索页数过大，则分页显示到第一页
            paginationInnerInterceptor.setDbType(DbType.H2);

            interceptor.addInnerInterceptor(paginationInnerInterceptor);
            return interceptor;
        }


    }

