package com.yuan.common;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/***
 *      整合Mybatis-Plus
 *      1. 导入依赖
 *      <dependencies>
 *     <dependency>
 *         <groupId>com.baomidou</groupId>
 *         <artifactId>mybatis-plus-boot-starter</artifactId>
 *         <version>3.5.1</version>
 *     </dependency>
 *
 *     2. 配置
 *          配置数据源： ①导入数据库驱动
 *                      ② 在每个模块下，配置数据库连接的基本信息
 *
 *     3. @MapperScan("com.yuan.guli.guliproduct.dao")
 *
 *     4.告诉映射文件的位置：
 *
 * **/
@SpringBootApplication
public class CommonApplication {
    public static void main(String[] args) {
        SpringApplication.run(CommonApplication.class,args);
    }
}
