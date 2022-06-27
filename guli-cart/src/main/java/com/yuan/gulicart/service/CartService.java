package com.yuan.gulicart.service;

import com.yuan.gulicart.vo.Cart;
import com.yuan.gulicart.vo.CartItem;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface CartService {

    /**
     * 将商品添加到购物项
     * */
    CartItem addCart(Long skuId, Integer num) throws ExecutionException, InterruptedException;

    /**
     * 获取某个购物项
     * */
    CartItem getCartItem(Long skuId);

    /**
     * 获取整个购物车
     * */
    Cart getCart() throws ExecutionException, InterruptedException;


    void cleanCart(String cartKey);

    void checkItem(Long skuId, Integer check);

    void changeCount(Long skuId, Integer num);

    void delItem(Long skuId);

    List<CartItem> getItems();

}
