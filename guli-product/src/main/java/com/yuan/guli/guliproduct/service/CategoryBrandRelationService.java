package com.yuan.guli.guliproduct.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuan.common.utils.PageUtils;
import com.yuan.guli.guliproduct.entity.CategoryBrandRelationEntity;
import com.yuan.guli.guliproduct.vo.BrandVo;

import java.util.List;
import java.util.Map;

/**
 * 品牌分类关联
 *
 * @author lzy
 * @email 1716827691@qq.com
 * @date 2022-03-07 21:33:41
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);



    /**
     * 获取品牌和属性关联列表的
     * */
    List<CategoryBrandRelationEntity> queryCBByBid(Long brandId);


    void  saveData(CategoryBrandRelationEntity categoryBrandRelationEntity);

    /**
     * 根据分类，查询品牌名称和id
     * */
    List<BrandVo> getCategoryBrand(Long catId);
}

