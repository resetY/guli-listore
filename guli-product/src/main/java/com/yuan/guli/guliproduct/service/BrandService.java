package com.yuan.guli.guliproduct.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuan.common.utils.PageUtils;
import com.yuan.guli.guliproduct.entity.BrandEntity;
import com.yuan.guli.guliproduct.vo.BrandVo;

import java.util.List;
import java.util.Map;

/**
 * 品牌
 *
 * @author lzy
 * @email 1716827691@qq.com
 * @date 2022-03-07 21:33:41
 */
public interface BrandService extends IService<BrandEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void updateBrandCategory(BrandEntity brandEntity);


    List<BrandEntity> getBrandByBrands(List<Long> brandId);
}

