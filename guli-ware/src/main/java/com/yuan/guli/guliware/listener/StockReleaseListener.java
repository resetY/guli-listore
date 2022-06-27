package com.yuan.guli.guliware.listener;

import com.rabbitmq.client.Channel;

import com.yuan.common.to.OrderTo;
import com.yuan.common.to.rmq.StockLockedTo;

import com.yuan.guli.guliware.service.WareSkuService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
@Service
@RabbitListener(queues = "stock.release.stock.queue") //监听延时队列
public class StockReleaseListener {
    @Autowired
    WareSkuService wareSkuService;

    @RabbitHandler
    public void handleStockText(StockLockedTo stockLockedTo, Message message, Channel channel) throws IOException {
        try{
            wareSkuService.unlockStock(stockLockedTo);
            //回复操作：
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
            System.out.println("解锁库存消息确认完毕");
        }catch (Exception e){
            //归队操作：解决消息，然后重新发回队列
            System.out.println("出现错误，解库消息重归队列");
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }
    }
    @RabbitHandler
    public void handleStockByOrder(OrderTo order, Message message, Channel channel) throws IOException {
        System.out.println("订单【"+order.getOrderSn()+"】已经关闭,准备解锁库存");
        try{
            wareSkuService.unlockStock(order); //重载方法进行解锁库存
            //回复操作：
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
            System.out.println("解锁库存消息确认完毕");
        }catch (Exception e){
            //归队操作：解决消息，然后重新发回队列
            System.out.println("出现错误，解库消息重归队列");
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }
    }


}
