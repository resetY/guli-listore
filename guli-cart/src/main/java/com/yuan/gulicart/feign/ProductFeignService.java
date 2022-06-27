package com.yuan.gulicart.feign;

import com.yuan.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;

@FeignClient("guli-product")
public interface ProductFeignService {

    @RequestMapping("/guliproduct/skuinfo/info/{skuId}")
    //@RequiresPermissions("guliproduct:skuinfo:info")
    R info(@PathVariable("skuId") Long skuId);

@RequestMapping("/guliproduct/skusaleattrvalue/skulist/{skuId}")
 List<String> getSkuAttrValue(@PathVariable("skuId")Long skuId);


    /**
     * 查询商品总价价格
     * **/
    @RequestMapping("/guliproduct/skuinfo/{skuId}/price")
    BigDecimal getPrice(@PathVariable("skuId") Long skuId);
}