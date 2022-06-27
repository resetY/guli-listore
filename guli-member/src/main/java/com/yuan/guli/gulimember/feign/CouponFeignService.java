package com.yuan.guli.gulimember.feign;

import com.yuan.common.utils.R;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *  调用远程服务的方式：
 *          ① 引入open-feign
 *          ② 编写接口，搞事springcloud这个接口是用来调用远程服务的
 *              在接口内声明要调用远程服务的抽象方法，Requestmapping路径要完整
 *              @FeignClient
 *          ③ 开启远程调用功能：@EnableFeignClients(basePackages = "com.yuan.guli.gulimember.feign")
 *                      选择开启远程服务的包
 * **/

@FeignClient("guli-coupon") //指定服务名
public interface CouponFeignService {
    @RequestMapping("/gulicoupon/coupon/member/list")
   R membercoupon();
}
