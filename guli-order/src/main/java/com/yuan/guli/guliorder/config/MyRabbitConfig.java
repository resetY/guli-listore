package com.yuan.guli.guliorder.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class MyRabbitConfig {
/**
 * 这个配置中，可使用@Bean的方式，直接把队列、交换机、Binding放入容器中
 *  直接默认生效
 *          Queue:队列
 *          Exchange：交换机
 *          Binding：绑定
 * ***/
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

    @Bean
    public Exchange orderEventExchange(){  //交互机
        //String name, boolean durable, boolean autoDelete, Map<String, Object> arguments
        TopicExchange topicExchange = new TopicExchange("order-event-exchange",true,
                false);
        return topicExchange;
    }



    @Bean
    public Queue orderReleaseQueue(){ //普通队列
        //队列名称、是否持久化、是否排他、是否自动删除、参数
        Map<String,Object>arguments = new HashMap<>();
        Queue queue = new Queue("order.release.order.queue",true,
                false ,false);
        return queue;
    }

    //String name, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments
    @Bean
    public Queue orderDelayQueue(){ //延时队列(死信队列)
        //队列名称、是否持久化、是否排他、是否自动删除、参数
        Map<String,Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange","order-event-exchange"); //死信路由：order-event-exchange
        arguments.put("x-dead-letter-routing-key","order.release.order");//死信路由键
        arguments.put("x-message-ttl",60000); //过期时间：一分钟进行测试

        Queue queue = new Queue("order.delay.queue",true,
                false ,false,arguments);
        return  queue;
    }
    @Bean
    public Binding orderCreateBinding(){
        //目的地、目的地类型、交换机、路由键
        Binding binding = new Binding("order.delay.queue",Binding.DestinationType.QUEUE,
                "order-event-exchange","order.create.order",null);
        return binding;
    }

    @Bean
    public Binding orderReleaseBinding(){
        //目的地、目的地类型、交换机、路由键
        Binding binding = new Binding("order.release.order.queue",Binding.DestinationType.QUEUE,
                "order-event-exchange","order.release.order",null);
        return binding;
    }


    /**
     * 解决订单自动取消队列延迟，导致库存无法解锁的问题
     *   绑定订单交换机  和  stock解锁队列
     *   订单释放  和 库存解锁 绑定
     * **/
    @Bean
    public Binding orderExchangeBindStock(){
            Binding binding = new Binding("stock.release.stock.queue", Binding.DestinationType.QUEUE,
                    "order-event-change","order.release.order.#",
                    null);
            return binding;
    }


    @Autowired
    RabbitTemplate rabbitTemplate;
    /**
     * 定制rabbitTemlate
     *          1. 配置true
     *          2. 设置确认回调
     * **/
    @PostConstruct //当MyRabbitConfig对象创建完毕，再执行这个方法
    public void initRabbitTemplate(){
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {

            /**
             * 消息回调的第一个时机：消息抵达服务
             *  @param correlationData 当前消息的唯一关联数据（消息的唯一id）
             *  @param  b 消息是否成功收到，只要消息抵达服务器，则返回true
             *  @param  s 如果发送失败，则这个是发送失败的原因
             * **/
            @Override
            public void confirm(CorrelationData correlationData, boolean b, String s) {

                /**
                 * 消息确认机制
                 * 每一个发送的消息都要在数据库进行记录
                 * ***/
                System.out.println("消息确认confirm.....唯一标识："+correlationData);
                System.out.println("消息确认confirm.....是否成功：："+b);
                System.out.println("消息确认confirm.....失败原因："+s);
            }
        });
        /**
         *  消息回调的第二个时机：消息抵达消息队列确认回调
         *  注意：消息投递成功，不会触发这个return回调
         *      @param message  投递失败的消息详细信息
         *      @param  i  回复的状态码
         *      @param  s 回复的文本内容
         *      @param  s1 发送的哪个交换机
         *      @param  s2 发送的时候指定的路由键
         *
         * **/
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int i, String s, String s1, String s2) {
                System.out.println("消息确认return.....失败消息："+message);
                System.out.println("消息确认return......状态：："+i);
                System.out.println("消息确认return......文本内容：："+s);
                System.out.println("消息确认return......交换机："+s1);
                System.out.println("消息确认return......路由键："+s2);
            }
        });



    }
}
