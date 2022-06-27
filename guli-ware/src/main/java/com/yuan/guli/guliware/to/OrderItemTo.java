package com.yuan.guli.guliware.to;


import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public  class OrderItemTo { //订单项
    private Long skuId; //商品id
    private Boolean check = true; //是否被选中
    private String title;
    private String image;
    private List<String> skuAttr; //商品套餐信息：6+128g
    private BigDecimal price;
    private Integer count;
    private BigDecimal totalPrice = new BigDecimal("0");
    private Boolean hasStock = true; //是否有货
    private BigDecimal weight; //商品重量：后续有需求再编写
}
