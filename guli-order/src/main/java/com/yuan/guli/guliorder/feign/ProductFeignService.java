package com.yuan.guli.guliorder.feign;

import com.yuan.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.annotation.Id;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.websocket.server.PathParam;
import java.math.BigDecimal;

@FeignClient("guli-product")
public interface ProductFeignService {

    @RequestMapping("/guliproduct/spuinfo/getSpu/{skuId}")
    R getSpuInfoByskuId(@PathVariable("skuId") Long skuId);
}
