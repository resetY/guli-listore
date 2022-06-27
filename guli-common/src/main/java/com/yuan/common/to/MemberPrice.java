/**
  * Copyright 2022 bejson.com 
  */
package com.yuan.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
会员价格
 */
@Data
public class MemberPrice {

    /**
     * 数据库id字段用Long，那么这里也用Long
     * */
    private Long id;
    private String name;
    private BigDecimal price;


}