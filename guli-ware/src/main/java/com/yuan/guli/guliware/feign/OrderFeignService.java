package com.yuan.guli.guliware.feign;

import com.yuan.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.websocket.server.PathParam;

@FeignClient("guli-order")
public interface OrderFeignService {
    @RequestMapping("/guliorder/order/getOrder/{orderSn}")
     R getOrderBySn(@PathVariable ("orderSn")String orderSn);
}
