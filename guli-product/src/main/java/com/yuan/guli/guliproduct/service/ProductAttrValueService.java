package com.yuan.guli.guliproduct.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuan.common.utils.PageUtils;
import com.yuan.guli.guliproduct.entity.ProductAttrValueEntity;

import java.util.List;
import java.util.Map;

/**
 * spu属性值
 *
 * @author lzy
 * @email 1716827691@qq.com
 * @date 2022-03-07 21:33:41
 */
public interface ProductAttrValueService extends IService<ProductAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveAttrValue(List<ProductAttrValueEntity> attrValueEntityList);

    List<ProductAttrValueEntity> listforSpu(Long spuId);

    void updateAttrValue(Long spuId, List<ProductAttrValueEntity> valueEntities);

    List<ProductAttrValueEntity> getAttrsByskuId(Long spuId);
}

