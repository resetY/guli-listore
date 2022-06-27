package com.yuan.guli.guliorder.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 封装订单提交数据的对象
 * **/
@Data
public class OrderSubmitVo {
        private  Long addId; //收货地址id
        //支付方式
        private Integer payType;//暂时只有在线支付或者货到付款
        //无需提交需要购买的商品，在购物车里面再获取进行选中的商品就行


    //订单提交需要带上防重令牌
    private String orderToken;
    //可进行价格对比，订单确认页面和顶单提交页面的数据进行价格对比，一样才通过
    private BigDecimal payPrice;
    //用户相关信息，在session中获取登录的用户

    private String node; //用户给订单的备注信息
}
