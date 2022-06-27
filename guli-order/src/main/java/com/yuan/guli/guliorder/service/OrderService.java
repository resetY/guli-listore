package com.yuan.guli.guliorder.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuan.common.utils.PageUtils;
import com.yuan.guli.guliorder.entity.OrderEntity;
import com.yuan.guli.guliorder.vo.*;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 订单
 *
 * @author lzy
 * @email 1716827691@qq.com
 * @date 2022-03-08 10:53:42
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException;

    SubmitResponseVo submitOrder(OrderSubmitVo submitOrder);

    OrderEntity getOrderBySn(String orderSn);

    void closeOrder(OrderEntity orderEntity);

    PayVo getOrderPay(String orderSn);

    PageUtils queryOrderList(Map<String, Object> params);


    String handlerPayResult(PayAsyncVo payVo);

}

