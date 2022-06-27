package com.yuan.guli.guliware.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 用韵存储运费和地址信息，传入前端进行渲染
 * */
@Data
public class FareVo {
        private  MemberAddressVo address;
        private BigDecimal farePrice;
}
