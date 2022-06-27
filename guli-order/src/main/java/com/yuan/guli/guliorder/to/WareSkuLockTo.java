package com.yuan.guli.guliorder.to;

import com.yuan.guli.guliorder.vo.OrderConfirmVo;
import lombok.Data;

import java.util.List;

/**
 * 规范：服务之间的传输用To
 * **/
@Data
public class WareSkuLockTo { //锁库存
    private String orderSn; //订单号
    private List<OrderConfirmVo.OrderItemVo> locks;//需要锁住的商品


}
