package com.yuan.guli.gulicoupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuan.common.utils.PageUtils;
import com.yuan.guli.gulicoupon.entity.CouponEntity;

import java.util.Map;

/**
 * 优惠券信息
 *
 * @author lzy
 * @email 1716827691@qq.com
 * @date 2022-03-08 10:03:15
 */
public interface CouponService extends IService<CouponEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

