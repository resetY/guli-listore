package com.yuan.guli.guliproduct.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.google.common.primitives.Longs;
import com.yuan.guli.guliproduct.service.BrandService;
import com.yuan.guli.guliproduct.vo.BrandVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.yuan.guli.guliproduct.entity.CategoryBrandRelationEntity;
import com.yuan.guli.guliproduct.service.CategoryBrandRelationService;
import com.yuan.common.utils.PageUtils;
import com.yuan.common.utils.R;



/**
 * 品牌分类关联
 *
 * @author lzy
 * @email 1716827691@qq.com
 * @date 2022-03-07 21:33:41
 */
@RestController
@RequestMapping("guliproduct/categorybrandrelation")
public class CategoryBrandRelationController {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;



    /**
     * 获取分类和品牌的关联的所有品牌
     */
    @GetMapping("/brands/list") //只能接收get请求
    //   @RequiresPermissions("guliproduct:categorybrandrelation:list")
    public R getCategoryBrand(@RequestParam(value = "catId",required = true)Long catId){
        List<BrandVo> categoryBrand = categoryBrandRelationService.getCategoryBrand(catId);
        return R.ok().put("data",categoryBrand );
    }



    /**
     * 获取当前品牌关联的所有分类列表功能
     */
    @GetMapping("/catelog/list") //只能接收get请求
    //   @RequiresPermissions("guliproduct:categorybrandrelation:list")
    public R categorylist(@RequestParam("brandId") Long brandId){
        List<CategoryBrandRelationEntity> categoryBrand = categoryBrandRelationService.queryCBByBid(brandId);
        return R.ok().put("data",categoryBrand );
    }

    /**
     * 保存平拍和分类关联：
     */
    @RequestMapping("/save")
    // @RequiresPermissions("guliproduct:categorybrandrelation:save")
    public R saveData(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
        categoryBrandRelationService.saveData(categoryBrandRelation);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("guliproduct:categorybrandrelation:update")
    public R update(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
        categoryBrandRelationService.updateById(categoryBrandRelation);

        return R.ok();
    }


    /**
     * 列表
     */
    @RequestMapping("/list")
 //   @RequiresPermissions("guliproduct:categorybrandrelation:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = categoryBrandRelationService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("guliproduct:categorybrandrelation:info")
    public R info(@PathVariable("id") Long id){
		CategoryBrandRelationEntity categoryBrandRelation = categoryBrandRelationService.getById(id);

        return R.ok().put("categoryBrandRelation", categoryBrandRelation);
    }

//    /**
//     * 保存
//     */
//    @RequestMapping("/save")
//   // @RequiresPermissions("guliproduct:categorybrandrelation:save")
//    public R save(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
//		categoryBrandRelationService.save(categoryBrandRelation);
//
//        return R.ok();
//    }



    /**
     * 删除
     */
    @RequestMapping("/delete")
   // @RequiresPermissions("guliproduct:categorybrandrelation:delete")
    public R delete(@RequestBody Long[] ids){
		categoryBrandRelationService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }




}
