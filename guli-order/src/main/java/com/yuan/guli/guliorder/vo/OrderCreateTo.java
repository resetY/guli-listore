package com.yuan.guli.guliorder.vo;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.yuan.guli.guliorder.entity.OrderEntity;
import com.yuan.guli.guliorder.entity.OrderItemEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
订单创建的数据
* **/
@Data
public class OrderCreateTo {
    private OrderEntity order;
    private List<OrderItemEntity> items;
    private BigDecimal pryPrice; //订单应付价格
    private BigDecimal fare; //运费
}
