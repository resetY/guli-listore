package com.yuan.gulicart.controller;

import com.yuan.gulicart.service.CartService;
import com.yuan.gulicart.vo.CartItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CartFeignController {
    @Autowired
    CartService cartService;

    @RequestMapping("/getItems")
    public List<CartItem> getItems(){ //获取所有选中的购物项
        System.out.println("被调用和访问-----------------");
        return cartService.getItems();
    }
}
