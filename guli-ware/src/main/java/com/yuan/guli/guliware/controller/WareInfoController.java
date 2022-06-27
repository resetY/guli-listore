package com.yuan.guli.guliware.controller;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.yuan.guli.guliware.vo.FareVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.yuan.guli.guliware.entity.WareInfoEntity;
import com.yuan.guli.guliware.service.WareInfoService;
import com.yuan.common.utils.PageUtils;
import com.yuan.common.utils.R;



/**
 * 仓库信息
 *
 * @author lzy
 * @email 1716827691@qq.com
 * @date 2022-03-08 10:57:57
 */
@RestController
@RequestMapping("guliware/wareinfo")
public class WareInfoController {
    @Autowired
    private WareInfoService wareInfoService;


    /**
     * 根据用户收货地址获取和计算运费
     * **/
    @GetMapping("/fare")
    public R getFare(@RequestParam("addId") Long addId){
        FareVo farePrice =  wareInfoService.getFare(addId);
        return R.ok().setData(farePrice);
    }


    /**
     * 列表
     */
    @RequestMapping("/list")
 //   @RequiresPermissions("guliware:wareinfo:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wareInfoService.queryInfoPages(params);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("guliware:wareinfo:info")
    public R info(@PathVariable("id") Long id){
		WareInfoEntity wareInfo = wareInfoService.getById(id);

        return R.ok().put("wareInfo", wareInfo);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
   // @RequiresPermissions("guliware:wareinfo:save")
    public R save(@RequestBody WareInfoEntity wareInfo){
		wareInfoService.save(wareInfo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("guliware:wareinfo:update")
    public R update(@RequestBody WareInfoEntity wareInfo){
		wareInfoService.updateById(wareInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
   // @RequiresPermissions("guliware:wareinfo:delete")
    public R delete(@RequestBody Long[] ids){
		wareInfoService.removeByIds(Arrays.asList(ids));
        return R.ok();
    }

}
