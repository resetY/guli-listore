package com.yuan.guli;





import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import javax.jnlp.BasicService;
import javax.swing.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class GuliProductApplicationTests {

//    @Autowired
//    BrandService brandService; //测试商品表
//    @Test
//    public void contextLoads() {
////        BrandEntity brandEntity = new BrandEntity();
////        brandEntity.setName("华伦天奴");
////        boolean save = brandService.save(brandEntity);
////        System.out.println(save);
////        System.out.println("保存成功");
//
//        //根据id进行修改的测试
////        BrandEntity brandEntity = new BrandEntity();
////        brandEntity.setBrandId(1L);
////        brandEntity.setDescript("adidas（阿迪达斯）创办于1949年，是德国运动用品制造商阿迪达斯AG成员公司。以其创办人阿道夫·阿迪·达斯勒（Adolf Adi Dassler）命名，1920年在黑措根奥拉赫开始生产鞋类产品。"); //修改描述信息
////        boolean b = brandService.updateById(brandEntity);
////        System.out.println(b);
//
//
//        //查询测试：
//        List<BrandEntity> list = brandService.list();
//        List<BrandEntity> list1 = brandService.list(new QueryWrapper<BrandEntity>().eq("name", "阿迪达斯"));//注入查询条件:name 为 阿迪达斯的
//        list.forEach(System.out::println);
//        list1.forEach(System.out::println);
//
//    }


    //测试：测试文件上传阿里云
//
//    @Autowired
//    OSS ossClient;
//    @Test
//    public void test() throws IOException {
//        InputStream inputStream = new FileInputStream("D:\\test.jpg");
//        ossClient.putObject("gulilzy","test3.jpg",inputStream);
//
//        ossClient.shutdown();
//        inputStream.close();
//        System.out.println("上传完毕");
//    }




    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    RedissonClient redissonClient;
    @Test
    public void test(){
        ValueOperations<String, String> helloword = redisTemplate.opsForValue();//一个简单的数据类型操作
       // helloword.set("one","Hello world");
        System.out.println("保存成功");
        String hello = helloword.get("one");
        System.out.println(hello);
    }

    @Test
    public void redissonTest(){
        System.out.println(redissonClient);
    }

}
