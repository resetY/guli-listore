package com.yuan.guli.guliorder.service.impl;

import com.rabbitmq.client.Channel;
import com.yuan.guli.guliorder.entity.OrderEntity;
import com.yuan.guli.guliorder.entity.OrderReturnReasonEntity;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuan.common.utils.PageUtils;
import com.yuan.common.utils.Query;

import com.yuan.guli.guliorder.dao.OrderItemDao;
import com.yuan.guli.guliorder.entity.OrderItemEntity;
import com.yuan.guli.guliorder.service.OrderItemService;


@Service("orderItemService")
  @RabbitListener(queues = {"hello-java-queue"})
public class OrderItemServiceImpl extends ServiceImpl<OrderItemDao, OrderItemEntity> implements OrderItemService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderItemEntity> page = this.page(
                new Query<OrderItemEntity>().getPage(params),
                new QueryWrapper<OrderItemEntity>()
        );

        return new PageUtils(page);
    }

//
//    /**
//     * //进行消息队列消息发送的监听
//     * 可以监听多个消息队列的内容接收
//     *   Channel:通道客户端
//     *   Queue：
//     * @RabbitHandler: 可以进行重载处理，接收不同类型的消息内容
//     * **/
//   // @RabbitListener(queues = {"hello-java-queue"})
//    @RabbitHandler
//    public void recieveMessage(Message message, OrderReturnReasonEntity returnReasonEntity,
//                               Channel channel){
////        System.out.println("检测到消息发送，消息内容为："+message);
////        System.out.println("检测到消息发送，消息内容体Body为："+message.getBody()); //消息体获取
//        System.out.println("接收到消息："+returnReasonEntity); //获取真正的消息体对应的内容的数据
//        long deliveryTag = message.getMessageProperties().getDeliveryTag();
//
//        //表示该消息队列已经被签收
//        //b：false为只签收当前的消息，不对其他进行批量签收
//      try {
//          //拒收消息:   channel.basicNack(deliveryTag,false,true); ,第三个参数为是否退还到队列中，false则直接丢弃该消息
//          channel.basicNack(deliveryTag,false,true);
//          //签收消息：
//          channel.basicAck(deliveryTag,false);
//      }catch (Exception e){
//          //网络中断，签收失败
//      }
//    }
//    @RabbitHandler
//  public void recieveMessage(OrderEntity orderEntity){
//       System.out.println("接收到消息："+orderEntity); //获取真正的消息体对应的内容的数据
//  }
}