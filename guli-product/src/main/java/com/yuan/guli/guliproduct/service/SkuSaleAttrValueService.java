package com.yuan.guli.guliproduct.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuan.common.utils.PageUtils;
import com.yuan.guli.guliproduct.entity.ProductAttrValueEntity;
import com.yuan.guli.guliproduct.entity.SkuSaleAttrValueEntity;
import com.yuan.guli.guliproduct.vo.SkuItemVo;

import java.util.List;
import java.util.Map;

/**
 * sku销售属性&值
 *
 * @author lzy
 * @email 1716827691@qq.com
 * @date 2022-03-07 21:33:41
 */
public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);




    List<SkuItemVo.SkuItemSaleAttrVo> getSaleAttrsBySpuid(Long spuId);

    List<String> getSkuAttrValue(Long skuId);
}

