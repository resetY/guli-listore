package com.yuan.guli.gulicoupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuan.common.to.SkuReductionTo;
import com.yuan.common.utils.PageUtils;
import com.yuan.guli.gulicoupon.entity.SkuFullReductionEntity;

import java.util.Map;

/**
 * 商品满减信息
 *
 * @author lzy
 * @email 1716827691@qq.com
 * @date 2022-03-08 10:03:15
 */
public interface SkuFullReductionService extends IService<SkuFullReductionEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSkuInfo(SkuReductionTo skuReductionTo);
}

