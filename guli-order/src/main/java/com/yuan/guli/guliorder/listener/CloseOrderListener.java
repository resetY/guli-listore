package com.yuan.guli.guliorder.listener;

import com.rabbitmq.client.Channel;
import com.yuan.common.to.rmq.StockLockedTo;
import com.yuan.guli.guliorder.entity.OrderEntity;
import com.yuan.guli.guliorder.service.OrderService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * 关闭订单的监听
 *
 * **/
@Service
@RabbitListener(queues = "order.release.order.queue")
public class CloseOrderListener {

    @Autowired
    OrderService orderService;
    @RabbitHandler
    public void closeOrderHandler(OrderEntity order, Message message, Channel channel) throws IOException {
        System.out.println("收到订单关闭的消息，准备关闭该订单："+order.getOrderSn());
       try{
           orderService.closeOrder(order);
           System.out.println("关闭订单成功......");
           channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
       }catch (Exception e){
           System.out.println("关闭失败，重归队列");
           channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
       }
    }
}
