package com.yuan.guli.guliorder.feign;

import com.yuan.guli.guliorder.vo.OrderConfirmVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

//调用购物车模块
@FeignClient("guli-cart")
public interface CartFeignService {
    @RequestMapping("/getItems") //这个OrderItemvo和购物车数据一致
    List<OrderConfirmVo.OrderItemVo> getItems();
}
