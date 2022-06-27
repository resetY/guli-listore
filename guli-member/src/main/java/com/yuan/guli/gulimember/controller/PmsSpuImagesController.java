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

import com.yuan.guli.gulimember.entity.PmsSpuImagesEntity;
import com.yuan.guli.gulimember.service.PmsSpuImagesService;
import com.yuan.common.utils.PageUtils;
import com.yuan.common.utils.R;



/**
 * spu图片
 *
 * @author lzy
 * @email 1716827691@qq.com
 * @date 2022-03-08 10:30:10
 */
@RestController
@RequestMapping("gulimember/pmsspuimages")
public class PmsSpuImagesController {
    @Autowired
    private PmsSpuImagesService pmsSpuImagesService;

    /**
     * 列表
     */
    @RequestMapping("/list")
 //   @RequiresPermissions("gulimember:pmsspuimages:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = pmsSpuImagesService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("gulimember:pmsspuimages:info")
    public R info(@PathVariable("id") Long id){
		PmsSpuImagesEntity pmsSpuImages = pmsSpuImagesService.getById(id);

        return R.ok().put("pmsSpuImages", pmsSpuImages);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
   // @RequiresPermissions("gulimember:pmsspuimages:save")
    public R save(@RequestBody PmsSpuImagesEntity pmsSpuImages){
		pmsSpuImagesService.save(pmsSpuImages);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("gulimember:pmsspuimages:update")
    public R update(@RequestBody PmsSpuImagesEntity pmsSpuImages){
		pmsSpuImagesService.updateById(pmsSpuImages);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
   // @RequiresPermissions("gulimember:pmsspuimages:delete")
    public R delete(@RequestBody Long[] ids){
		pmsSpuImagesService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
