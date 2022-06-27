package com.yuan.guli.guliware.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class MyRabbitConfig {

    /**
     * 配置：进行json数据的配置，将消息队列的传输数据，全部转化为json格式
     * */
    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }


    /**
     * 注意：一旦启动后，创建完队列
     * RabbitMq有这个队列，即使你再次在这里进行一些配置修改，那么也不会对mq里面的进行覆盖
     * **/

    //String name, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments

    @Bean
    public Exchange stockEventExchange(){ //交换机
        //String name, boolean durable, boolean autoDelete, Map<String, Object> arguments
        TopicExchange topicExchange = new TopicExchange("stock-event-exchange",true,false);
        return topicExchange;
    }


    @Bean
    public Queue stockReleaseQueue(){  //死信队列
        //队列名称、是否持久化、是否排他、是否自动删除、参数
        Map<String,Object>arguments = new HashMap<>();
        Queue queue = new Queue("stock.release.stock.queue",true,
                false ,false);
        return queue;
    }

    @Bean
    public Queue stockDelayQueue(){ //延时队列
        //队列名称、是否持久化、是否排他、是否自动删除、参数
        Map<String,Object> arguments = new HashMap<>();

        //延迟队列的特性：
        arguments.put("x-dead-letter-exchange","stock-event-exchange"); //死信路由：order-event-exchange
        arguments.put("x-dead-letter-routing-key","stock.release");//死信路由键
        arguments.put("x-message-ttl",120000); //过期时间：两分钟进行测试

        Queue queue = new Queue("stock.delay.queue",true,
                false ,false,arguments);

        return  queue;
    }


    @Bean
    public Binding stockRelaseBinding(){  //和库存锁定的绑定关系
        //目的地、目的地类型、交换机、路由键
        Binding binding = new Binding("stock.release.stock.queue",Binding.DestinationType.QUEUE,
                "stock-event-exchange","stock.release.#",null);
        return binding;
    }
    @Bean
    public Binding stockDelBinding(){ //延时队列的绑定关系
        //目的地、目的地类型、交换机、路由键
        Binding binding = new Binding("stock.delay.queue",Binding.DestinationType.QUEUE,
                "stock-event-exchange","stock.locked",null);
        return binding;
    }

}
