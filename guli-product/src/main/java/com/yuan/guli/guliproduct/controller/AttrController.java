package com.yuan.guli.guliproduct.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.yuan.guli.guliproduct.entity.ProductAttrValueEntity;
import com.yuan.guli.guliproduct.service.ProductAttrValueService;
import com.yuan.guli.guliproduct.vo.AttrRespVo;
import com.yuan.guli.guliproduct.vo.AttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.yuan.guli.guliproduct.entity.AttrEntity;
import com.yuan.guli.guliproduct.service.AttrService;
import com.yuan.common.utils.PageUtils;
import com.yuan.common.utils.R;



/**
 * 商品属性
 *
 * @author lzy
 * @email 1716827691@qq.com
 * @date 2022-03-07 21:33:41
 */
@RestController
@RequestMapping("guliproduct/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;


//    /**
//     * 列表
//     */
//    @RequestMapping("/base/list/{catelogId}")
//    //   @RequiresPermissions("guliproduct:attr:list")
//    public R baseList(@RequestParam Map<String, Object> params,
//                  @PathVariable("catelogId") Long catelogId){
//        PageUtils page = attrService.queryBaseAttr(params,catelogId);
//        return R.ok().put("page", page);
//    }


//    /**
//     * 接口：分类销售属性显示
//     */
//    @RequestMapping("/sale/list/{attrType}")
//    public R soleAttrList(@RequestParam Map<String, Object> params,
//                          @PathVariable("attrType") Integer attrType){
//        PageUtils page = attrService.querySaleAttr(params, attrType);
//        return R.ok().put("page",page);
//    }
//    /**
//     * 销售属性列表：使用分类id查询
//     */
//    @RequestMapping("/sale/list/{catelogId}")
//    //   @RequiresPermissions("guliproduct:attr:list")
//    public R saleList(@RequestParam Map<String, Object> params,
//                      @PathVariable("catelogId") Long catelogId){
//        PageUtils page = attrService.saleList(params,catelogId);
//        return R.ok().put("page", page);
//    }

    @RequestMapping("/{attrType}/list/{catelogId}")
    public R baseAttrList(@RequestParam Map<String, Object> params,
                          @PathVariable("attrType")String attrType,
                          @PathVariable("catelogId")Long catelogId){

            PageUtils page = attrService.queryBaseOrSale(params,catelogId,attrType);
            return R.ok().put("page", page);
    }

    /**
     * 回显信息
     */
    @RequestMapping("/info/{attrId}")
    //@RequiresPermissions("guliproduct:attr:info")
    public R info(@PathVariable("attrId") Long attrId){
        AttrRespVo info = attrService.getInfo(attrId);
        return R.ok().put("attr", info);
    }

/**
 *
 * 显示spu规格参数所有数据的
 * **/
    @Autowired
    ProductAttrValueService attrValueService;
    @GetMapping("/base/listforspu/{spuId}")
    public R listforspu(@PathVariable("spuId") Long  spuId){
        List<ProductAttrValueEntity> data = attrValueService.listforSpu(spuId);
        return  R.ok().put("data",data);
    }



    @RequestMapping("/save")
    // @RequiresPermissions("guliproduct:attr:save")
    public R save(@RequestBody AttrVo attrVo){
        attrService.saveAttrVo(attrVo);
        return R.ok();
    }
//
//    /**
//     * 保存
//     */
//    @RequestMapping("/save")
//    // @RequiresPermissions("guliproduct:attr:save")
//    public R save(@RequestBody AttrEntity attr){
//        attrService.saveAttr(attr);
//        return R.ok();
//    }
    /**
     * 列表
     */
    @RequestMapping("/list")
    //   @RequiresPermissions("guliproduct:attr:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = attrService.queryPage(params);
        return R.ok().put("page", page);
    }


    /**
     * spu管理维护
     */
    @RequestMapping("/update/{spuId}")
    //@RequiresPermissions("guliproduct:attr:update")
    public R updateSpu( @PathVariable("spuId")Long spuId,
                        @RequestBody List<ProductAttrValueEntity> valueEntities){
        attrValueService.updateAttrValue(spuId,valueEntities);
        return R.ok();
    }



    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("guliproduct:attr:update")
    public R update(@RequestBody AttrVo attrVo){
		attrService.updateAttr(attrVo);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
   // @RequiresPermissions("guliproduct:attr:delete")
    public R delete(@RequestBody Long[] attrIds){
		attrService.removeByIds(Arrays.asList(attrIds));
        return R.ok();
    }

}

