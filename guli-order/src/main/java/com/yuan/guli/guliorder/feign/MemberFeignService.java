package com.yuan.guli.guliorder.feign;

import com.yuan.guli.guliorder.vo.OrderConfirmVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient("guli-member")
public interface MemberFeignService {
    //查询某个用户的所有地址信息
    @RequestMapping("/gulimember/memberreceiveaddress/{memberId}/address")
    List<OrderConfirmVo.MemberAddressVo> getAddress(@PathVariable("memberId") Long memberId);
        }
