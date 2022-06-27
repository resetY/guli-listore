package com.yuan.guli.guliproduct.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.yuan.common.valid.AddGroup;
import com.yuan.common.valid.UpdateGroup;
import com.yuan.common.valid.UpdateStatusGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yuan.guli.guliproduct.entity.BrandEntity;
import com.yuan.guli.guliproduct.service.BrandService;
import com.yuan.common.utils.PageUtils;
import com.yuan.common.utils.R;


/**
 * 品牌
 *
 * @author lzy
 * @email 1716827691@qq.com
 * @date 2022-03-07 21:33:41
 */
@RestController
@RequestMapping("guliproduct/brand")
public class BrandController {
    @Autowired
    private BrandService brandService;

    /**
     * 列表
     */
    @RequestMapping("/list")
 //   @RequiresPermissions("guliproduct:brand:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = brandService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{brandId}")
    //@RequiresPermissions("guliproduct:brand:info")
    public R info(@PathVariable("brandId") Long brandId){
		BrandEntity brand = brandService.getById(brandId);

        return R.ok().put("brand", brand);
    }

    /**
     * 远程调用的获取品牌信息
     */
    @RequestMapping("/brandInfo/{brandId}")
    //@RequiresPermissions("guliproduct:brand:info")
    public R getBrandByBrandId(@PathVariable("brandId") Long brandId){
        BrandEntity brand = brandService.getById(brandId);
        return R.ok().setData(brand);
    }

    /**
     * 保存
     * BindingResult:校验结果的验证
     */
    @RequestMapping("/save")
   // @RequiresPermissions("guliproduct:brand:save")
    public R save(@Validated(AddGroup.class)  @RequestBody BrandEntity brand){
        brandService.save(brand);
        return R.ok();
    }

    /**
     * 修改
     * @Validated注解 分配校验分组
     * 在修改品牌brand的同时，修改关联表中的数据 (不适用级联查询)
     */
    @RequestMapping("/update")
    //@RequiresPermissions("guliproduct:brand:update")
    public R update(@Validated(UpdateGroup.class) @RequestBody BrandEntity brand){
        brandService.updateBrandCategory(brand);
        return R.ok();
    }

    /**
     * 修改状态
     * @Validated注解 分配校验分组
     */
    @RequestMapping("/update/status")
    //@RequiresPermissions("guliproduct:brand:update")
    public R updatestatus(@Validated(UpdateStatusGroup.class) @RequestBody BrandEntity brand){
        brandService.updateById(brand);
        return R.ok();
    }


    /**
     * 删除
     */
    @RequestMapping("/delete")
   // @RequiresPermissions("guliproduct:brand:delete")
    public R delete(@RequestBody Long[] brandIds){
		brandService.removeByIds(Arrays.asList(brandIds));

        return R.ok();
    }




    @RequestMapping("/infos")
    public R infos(@RequestParam("brandIds") List<Long> brandId){
           List<BrandEntity> brandEntities = brandService.getBrandByBrands(brandId);
             return  R.ok().put("brand",brandEntities);
    }
}
