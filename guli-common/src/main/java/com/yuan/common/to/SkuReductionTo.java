package com.yuan.common.to;


import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 保存Sku信息的远程调用
 * */
@Data
public class SkuReductionTo {


    //sku的id
    private Long skuId;

    //优惠信息：
    private Integer fullCount;
    private BigDecimal discount; //满减折扣
    private Integer countStatus;
    private BigDecimal fullPrice;
    private BigDecimal reducePrice;
    private Integer  priceStatus;

    //会员价格相关信息：to里面需要有MemberPrice类
    private List<MemberPrice> memberPrice;
}
