package com.yuan.guli.guliware.feign;

import com.yuan.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("guli-product")
public interface ProductFeign {

    /**
     * 信息
     */
    @RequestMapping("/guliproduct/spuinfo/info/{id}")
   R info(@PathVariable("id") Long id);
    /**
     * 远程调用的获取品牌信息
     */
    @RequestMapping("/guliproduct/brand/brandInfo/{brandId}")
    //@RequiresPermissions("guliproduct:brand:info")
  R getBrandByBrandId(@PathVariable("brandId") Long brandId);
}
