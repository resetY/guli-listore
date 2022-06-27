package com.yuan.guli.guliorder.feign;

import com.yuan.common.utils.R;
import com.yuan.guli.guliorder.to.WareSkuLockTo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("guli-ware")
public interface WareFeignService {
    /**
     * 被远程调用：查询skuid对应的商品是否有库存
     * **/
    @RequestMapping("/guliware/waresku/hasstock") //        //应该返回的值：sku—id  和 stock
     R getHasStock(@RequestBody List<Long> skuIds);
    /**
     * 根据用户收货地址获取和计算运费
     * **/
    @GetMapping("/guliware/wareinfo/fare")
 R getFare(@RequestParam("addId") Long addId);

    /**
     * 创建订单后的库存锁定操作
     * @param  to 传过来的需要锁定的数据
     * **/

    @RequestMapping("/guliware/waresku/stockLock")
 R stockLock(@RequestBody WareSkuLockTo to);
}
