package com.yuan.guli.guliorder;


import com.alibaba.fastjson.JSON;
import com.yuan.guli.guliorder.entity.OrderReturnApplyEntity;
import com.yuan.guli.guliorder.entity.OrderReturnReasonEntity;
import lombok.Data;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class GuliOrderApplicationTests {

    /**
     * 测试高级消息队列
     * 创建Exchange、Queue、Binding
     * **/



    @Autowired
    RabbitTemplate rabbitTemplate;



    @Autowired
    AmqpAdmin amqpAdmin;
    @Test
    public void contextLoads() {

        //1.交换机名字、是否持久化、是否自动删除
        DirectExchange directExchange = new DirectExchange("hello-java-exchange",
                true,false);
        amqpAdmin.declareExchange(directExchange);
        log.info("exchange{}交换机完成","hello-java-exchange");
        System.out.println("完成交换机创建");
    }

    @Test
    public void createQueue(){
         //队列：名称、是否持久化、是否排他()、是否自动删除、添加参数
        Queue queue1 = new Queue("hello-java-queue",true,
                                false,false);
        amqpAdmin.declareQueue(queue1);
        System.out.println("完成消息队列创建");

    }

    @Test
    public void createBinding(){
        //参数1：目的地
        //参数2：目的地类型
        //参数3：交换机
        //参数4：路由键
        //参数5：自定义参数
        Binding binding = new Binding("hello-java-queue", Binding.DestinationType.QUEUE,
                "hello-java-exchange","hello.java", null);
        amqpAdmin.declareBinding(binding);
        System.out.println("完成交换机和消息队列的绑定");

    }

    /**
     * 发送消息测试
     * */
    @Test
    public void Messagetest(){

        byte [] body = new byte[]{};
        //Message message = new Message();
        rabbitTemplate.convertAndSend("hello-java-exchange",
                "hello.java","我是java整合发送过去的消息(发送给消息队列的内容)");
        System.out.println("发送完成");
    }

    //退货原因实体类
    OrderReturnReasonEntity orderReturnReasonEntity = new OrderReturnReasonEntity();

    /**
     * 消息队列发送对象
     * **/
    @Test
    public void MessageTest(){
        orderReturnReasonEntity .setId(2L);
        orderReturnReasonEntity .setCreateTime(new Date());
        orderReturnReasonEntity.setName("json");
        //Message message = new Message();
    //   String json_order = JSON.toJSONString(orderReturnReasonEntity);
        rabbitTemplate.convertAndSend("hello-java-exchange",
                "hello.java", orderReturnReasonEntity);
        System.out.println("发送完成");

    }

}
