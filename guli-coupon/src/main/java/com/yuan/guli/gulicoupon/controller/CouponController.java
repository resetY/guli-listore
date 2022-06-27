package com.yuan.guli.gulicoupon.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yuan.guli.gulicoupon.entity.CouponEntity;
import com.yuan.guli.gulicoupon.service.CouponService;
import com.yuan.common.utils.PageUtils;
import com.yuan.common.utils.R;

import javax.security.auth.login.Configuration;


/**
 * 优惠券信息
 *
 * @author lzy
 * @email 1716827691@qq.com
 * @date 2022-03-08 10:03:15
 */
@RefreshScope //刷新配置，配合配置中心，进行动态刷新
@RestController
@RequestMapping("gulicoupon/coupon")
public class CouponController {
    @Autowired
    private CouponService couponService;

    //会员的优惠券功能模拟：测试远程调用Feign，可以让会员模块调用优惠券功能，查看优惠券
    @RequestMapping("/member/list")
    public R membercoupon(){  //这里的R类似于JsonResult<>
        CouponEntity couponEntity = new CouponEntity(); //一个优惠券实体类bean声明
        couponEntity.setCouponName("满100减5");
        return  R.ok().put("coupons", Arrays.asList(couponEntity));
    }


    //2. 配置中心的测试：
    @Value("${mn.user.name}")
    String name;
    @Value("${user.age}")
    Integer age;
    @RequestMapping("/test")
    public R nacosTest(){
        return  R.ok().put("name",name).put("age",age);
    }



    /**
     * 列表
     */
    @RequestMapping("/list")
 //   @RequiresPermissions("gulicoupon:coupon:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = couponService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("gulicoupon:coupon:info")
    public R info(@PathVariable("id") Long id){
		CouponEntity coupon = couponService.getById(id);

        return R.ok().put("coupon", coupon);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
   // @RequiresPermissions("gulicoupon:coupon:save")
    public R save(@RequestBody CouponEntity coupon){
		couponService.save(coupon);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("gulicoupon:coupon:update")
    public R update(@RequestBody CouponEntity coupon){
		couponService.updateById(coupon);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
   // @RequiresPermissions("gulicoupon:coupon:delete")
    public R delete(@RequestBody Long[] ids){
		couponService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
