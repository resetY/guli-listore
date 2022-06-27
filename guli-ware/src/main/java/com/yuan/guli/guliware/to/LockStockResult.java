package com.yuan.guli.guliware.to;

import lombok.Data;

import java.util.Map;

/**
 * 库存锁定结果的返回
 * */
@Data
public class LockStockResult {
     private    Long skuId; //锁定的商品id
    private Integer num; //锁定的件数
    private Boolean locked; //是否锁定成功
}
