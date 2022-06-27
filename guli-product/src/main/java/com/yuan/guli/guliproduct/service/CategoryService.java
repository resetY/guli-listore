package com.yuan.guli.guliproduct.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuan.common.utils.PageUtils;
import com.yuan.guli.guliproduct.entity.BrandEntity;
import com.yuan.guli.guliproduct.entity.CategoryEntity;
import com.yuan.guli.guliproduct.web.vo.Catelog2Vo;

import java.util.List;
import java.util.Map;

/**
 * 商品三级分类
 *
 * @author lzy
 * @email 1716827691@qq.com
 * @date 2022-03-07 21:33:41
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 将分类查询到，并且组装未树形结构的方法
     * */
    List<CategoryEntity> listWithTree();


    void  removeMenuByIds(List<Long>cIds);

    /**
     * 级联更新
     * */
    void updateBrandCategory(CategoryEntity categoryEntity);


    /**
     * 所有一级分类
    * */
    List<CategoryEntity> getLevel1Categorys();


    /**
     查询json数据的category三级分类数据
    * **/
    Map<String,List<Catelog2Vo>> getcateJsonFormDb();


    /**
     * 通过缓存获取到三级分类数据
     * */
    Map<String, List<Catelog2Vo>> getcateJson();

    /**
     * 缓存注解方式获取三级分类数据：
     * */
   Map<String,List<Catelog2Vo>> getCatelogJson();


   Map<String, List<Catelog2Vo>> getmysqlDb();

}

