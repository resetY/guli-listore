package com.yuan.guli.guliproduct.feign;

import com.yuan.common.to.SkuHasStockTo;
import com.yuan.common.utils.R;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient("guli-ware")
public interface WareFeignService {

    /**
     * 被远程调用：查询skuid对应的商品是否有库存
     * **/
    @RequestMapping("guliware/waresku/hasstock")
    R getHasStock(@RequestBody List<Long> skuIds);

}
