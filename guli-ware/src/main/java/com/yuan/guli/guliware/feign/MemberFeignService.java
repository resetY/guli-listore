package com.yuan.guli.guliware.feign;

import com.yuan.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("guli-member")
public interface MemberFeignService {

    /**
     * 信息
     */
    @RequestMapping("/gulimember/memberreceiveaddress/info/{id}")
    R info(@PathVariable("id") Long id);
}
