package com.yuan.guli.guliproduct.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yuan.guli.guliproduct.entity.CategoryEntity;
import com.yuan.guli.guliproduct.service.CategoryService;
import com.yuan.common.utils.PageUtils;
import com.yuan.common.utils.R;

/**
 * 商品三级分类
 *
 * @author lzy
 * @email 1716827691@qq.com
 * @date 2022-03-07 21:33:41
 */
@RestController
@RequestMapping("guliproduct/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;




    /**
     * 查询所有分类以及子分类，以树形结构进行组装
     */

 //   @RequiresPermissions("guliproduct:category:list")
    @RequestMapping("/tree")  //分类树
    public R list(){
        List<CategoryEntity> categoryEntities = categoryService.listWithTree();
        return R.ok().put("page", categoryEntities);
    }

    /**
     * 保存菜单信息
     * 前端直接进行提交请求 即可，因为前端字段名和后端一致
     */
    @RequestMapping("/save")
    // @RequiresPermissions("guliproduct:category:save")
    public R save(@RequestBody CategoryEntity category){
        categoryService.save(category);
        return R.ok();
    }

    /**
     * 删除菜单：
     * @RequestBody：获取请求体，post请求才有请求体，get没有，必须发送post请求
     * SpringMVc自动将请求体数据(json) 转化为 对应对象  Long[]catIds
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("guliproduct:category:delete")
    public R delete(@RequestBody Long[] catIds){
        categoryService.removeByIds(Arrays.asList(catIds));
        return R.ok();
    }

    /**
     * 删除菜单：
     * @RequestBody：获取请求体，post请求才有请求体，get没有，必须发送post请求
     * SpringMVc自动将请求体数据(json) 转化为 对应对象  Long[]catIds
     *
     *
     */
    @RequestMapping("/remove")
    // @RequiresPermissions("guliproduct:category:delete")
    public R remove(@RequestBody Long[] catIds){
        categoryService.removeMenuByIds(Arrays.asList(catIds));
        return R.ok();
    }



    /**
     * 根据 id进行查询：
     */
    @RequestMapping("/info/{catId}")
    //@RequiresPermissions("guliproduct:category:info")
    public R info(@PathVariable("catId") Long catId){
		CategoryEntity category = categoryService.getById(catId);

        return R.ok().put("category", category);
    }


    /**
     * 修改:也要加上级联更新
     */
    @RequestMapping("/update")
    //@RequiresPermissions("guliproduct:category:update")
    public R update(@RequestBody CategoryEntity category){
		categoryService.updateById(category);
        return R.ok();
    }



    /**
     * 拖拽后进行数据变化的接口：category：前端传输过来的需要进行数据改变的 节点集合
     *              对节点集合内的 所有菜单进行数据改变
     * **/
    @RequestMapping("/update/sort")
    public R updateSort(@RequestBody CategoryEntity [] category){
        categoryService.updateBatchById(Arrays.asList(category));
        return R.ok();
    }


}


