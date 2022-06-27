package com.yuan.guli.guliware.error;

public class NoStockException extends RuntimeException{
    private Long skuId;
    private String skuName;

    public NoStockException() {
        super("库存不存在");
    }

    public NoStockException(Long skuId, String skuName) {
     super(skuId+"号商品【"+skuName+"】没有库存！！！   ");
    }
}
