package com.yuan.guli.guliorder.vo;

import com.yuan.guli.guliorder.entity.OrderEntity;
import lombok.Data;

@Data  //下单操作返回数据vo
public class SubmitResponseVo {

    private OrderEntity order;//下单成功返回的订单信息
    private Integer code;//错误状态码，0为成功
}
