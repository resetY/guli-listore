package com.yuan.guli.guliware.to;


import lombok.Data;

import java.util.List;

/**
 * 规范：服务之间的传输用To
 * **/
@Data
public class WareSkuLockTo { //锁库存
    private String orderSn; //订单号
    private List<OrderItemTo> locks;//需要锁住的商品
}
