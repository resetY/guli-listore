package com.yuan.guli.gulimember.controller;

import java.util.Arrays;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yuan.guli.gulimember.entity.PmsSpuInfoDescEntity;
import com.yuan.guli.gulimember.service.PmsSpuInfoDescService;
import com.yuan.common.utils.PageUtils;
import com.yuan.common.utils.R;



/**
 * spu信息介绍
 *
 * @author lzy
 * @email 1716827691@qq.com
 * @date 2022-03-08 10:30:09
 */
@RestController
@RequestMapping("gulimember/pmsspuinfodesc")
public class PmsSpuInfoDescController {
    @Autowired
    private PmsSpuInfoDescService pmsSpuInfoDescService;

    /**
     * 列表
     */
    @RequestMapping("/list")
 //   @RequiresPermissions("gulimember:pmsspuinfodesc:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = pmsSpuInfoDescService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{spuId}")
    //@RequiresPermissions("gulimember:pmsspuinfodesc:info")
    public R info(@PathVariable("spuId") Long spuId){
		PmsSpuInfoDescEntity pmsSpuInfoDesc = pmsSpuInfoDescService.getById(spuId);

        return R.ok().put("pmsSpuInfoDesc", pmsSpuInfoDesc);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
   // @RequiresPermissions("gulimember:pmsspuinfodesc:save")
    public R save(@RequestBody PmsSpuInfoDescEntity pmsSpuInfoDesc){
		pmsSpuInfoDescService.save(pmsSpuInfoDesc);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("gulimember:pmsspuinfodesc:update")
    public R update(@RequestBody PmsSpuInfoDescEntity pmsSpuInfoDesc){
		pmsSpuInfoDescService.updateById(pmsSpuInfoDesc);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
   // @RequiresPermissions("gulimember:pmsspuinfodesc:delete")
    public R delete(@RequestBody Long[] spuIds){
		pmsSpuInfoDescService.removeByIds(Arrays.asList(spuIds));

        return R.ok();
    }

}
