package com.yuan.guli.gulimember.feign;

import com.yuan.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient("guli-order")
public interface OrderFeignService {

    /**
     * 分页展示订单列表
     */
    @RequestMapping("/guliorder/order/orderList")
   R queryPageWithItem(@RequestBody Map<String, Object> params);
}
