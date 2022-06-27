package com.yuan.guli.es.feign;

import com.yuan.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("guli-product") //调用商品服务
public interface ProductFeign {
   @RequestMapping("guliproduct/attr/info/{attrId}")
    public R info(@PathVariable("attrId") Long attrId);

    @RequestMapping("/guliproduct/brand/infos")
    public R infos(@RequestParam("brandIds") List<Long> brandId);
}
