package com.yuan.common.to;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SpuBoundsTo {
    //需要的数据：
    private Long spuId;
    //Bounds中的两个属性：
    private BigDecimal buyBounds;
    private BigDecimal growBounds;
}
