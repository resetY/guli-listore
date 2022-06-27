package com.yuan.guli.guliproduct.feign;

import com.yuan.common.to.es.SkuEsModel;
import com.yuan.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient("guli-search")
public interface SearchFeignService {

    @RequestMapping("/search/save")
    R UpProduct(@RequestBody List<SkuEsModel> esModels);


}
