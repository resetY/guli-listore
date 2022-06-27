package com.yuan.guli.guliproduct.feign;

import com.yuan.common.to.SkuReductionTo;
import com.yuan.common.to.SpuBoundsTo;
import com.yuan.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

@FeignClient("guli-coupon") //要远程调用的服务:spu的优惠券共服务
public interface CouponFeignService {




    /**
     *  SpringCloud在进行远程调用的适合，交互都是以json进行的
     *          然后再转化为对象形式，因此，只要交互的数据json中的key对的上，
     *          那么即便是接收的对象不同，也可进行远程调用成功
     *              所以，这里远程调用的接口参数，不需要是 SpuBoundsTo 类型
     *   远程调用步骤：
     *              1. @RequestBody将对象转化为json
     *              2. springcloud 注册中心中寻找 要调用服务的名字，然后发送请求
     *                          将上一步的json数据放在请求体位置，发送请求
     *              3. 对方服务收到请求体的json数据,将json数据转成对方服务的对象
     *              （@RequersBody SpuBrandsEntity spubrounds）
     *
     * **/
    @PostMapping("/gulicoupon/spubounds/save") //调用的控制层接口的完整路径
    R saveSpuBounds(@RequestBody SpuBoundsTo spuBoundsTo);


    @PostMapping("/gulicoupon/skufullreduction/saveInfo")
    R saveSkuReduction(@RequestBody SkuReductionTo skuReductionTo);
}
