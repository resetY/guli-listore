package com.yuan.guli.guliproduct.controller;

import java.util.Arrays;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.yuan.guli.guliproduct.vo.SpuSaveVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.yuan.guli.guliproduct.entity.SpuInfoEntity;
import com.yuan.guli.guliproduct.service.SpuInfoService;
import com.yuan.common.utils.PageUtils;
import com.yuan.common.utils.R;

import javax.websocket.server.PathParam;


/**
 * spu信息
 *
 * @author lzy
 * @email 1716827691@qq.com
 * @date 2022-03-07 21:33:41
 */
@RestController
@RequestMapping("guliproduct/spuinfo")
public class SpuInfoController {
    @Autowired
    private SpuInfoService spuInfoService;


    @RequestMapping("/getSpu/{skuId}")
    R getSpuInfoByskuId(@PathVariable("skuId") Long skuId){
       SpuInfoEntity spuInfoEntity =  spuInfoService.getSpuInfoByskuId(skuId);
        return  R.ok().setData(spuInfoEntity);
    }


    /**
     * spu复杂检索：查询等功能
     * 传入数据：商品分类id、品牌分组id、状态或者关键字key
     */
    @RequestMapping("/list")
 //   @RequiresPermissions("guliproduct:spuinfo:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = spuInfoService.querySpuinfoPage(params);
        return R.ok().put("page", page);
    }




    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("guliproduct:spuinfo:info")
    public R info(@PathVariable("id") Long id){
		SpuInfoEntity spuInfo = spuInfoService.getById(id);

        return R.ok().put("spuInfo", spuInfo);
    }

    /**
     * 需要保存sku spu 数据，需要保存的表非常的多，需要关联的也多
     */
    @RequestMapping("/save")
   // @RequiresPermissions("guliproduct:spuinfo:save")
    public R save(@RequestBody SpuSaveVo vo){
		spuInfoService.saveSpuInfo(vo);
        return R.ok();
    }

    /**
     * spu商品上架到商城接口
     * **/
    @PostMapping("{spuId}/up")
    public R up(@PathVariable("spuId") Long spuId){
        spuInfoService.upProduct(spuId);
        return R.ok();
    }



    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("guliproduct:spuinfo:update")
    public R update(@RequestBody SpuInfoEntity spuInfo){
		spuInfoService.updateById(spuInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
   // @RequiresPermissions("guliproduct:spuinfo:delete")
    public R delete(@RequestBody Long[] ids){
		spuInfoService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
