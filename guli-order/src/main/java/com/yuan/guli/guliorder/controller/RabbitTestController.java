package com.yuan.guli.guliorder.controller;

import com.yuan.guli.guliorder.entity.OrderEntity;
import com.yuan.guli.guliorder.entity.OrderReturnReasonEntity;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.UUID;

/**
 * 进行RabbitMq测试的控制层类
 * **/
@RestController
public class RabbitTestController {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @RequestMapping("/test")   //num设置为发送消息队列的次数
    public String test(@RequestParam(value = "num",defaultValue = "10")Integer num){
for (int i=0;i<num;i++){
    if(i%2==0){
        OrderReturnReasonEntity orderReturnReasonEntity = new OrderReturnReasonEntity();
        orderReturnReasonEntity .setId(50L);
        orderReturnReasonEntity .setCreateTime(new Date());
        orderReturnReasonEntity.setName("json"+i);
        //Message message = new Message();
        //   String json_order = JSON.toJSONString(orderReturnReasonEntity);
        rabbitTemplate.convertAndSend("hello-java-exchange",
                "hello.java", orderReturnReasonEntity,new CorrelationData(UUID.randomUUID().toString()));
      System.out.println("发送完成");
    }else{
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderSn(UUID.randomUUID().toString());
        rabbitTemplate.convertAndSend("hello-java-exchange",
                "hello.java",orderEntity,new CorrelationData(UUID.randomUUID().toString()));
        System.out.println("发送完成");
    }
}
return "ok";
    }

}
