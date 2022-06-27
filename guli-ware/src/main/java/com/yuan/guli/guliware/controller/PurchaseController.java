package com.yuan.guli.guliware.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.yuan.guli.guliware.vo.DoneVo;
import com.yuan.guli.guliware.vo.MergeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.yuan.guli.guliware.entity.PurchaseEntity;
import com.yuan.guli.guliware.service.PurchaseService;
import com.yuan.common.utils.PageUtils;
import com.yuan.common.utils.R;



/**
 * 采购信息
 *
 * @author lzy
 * @email 1716827691@qq.com
 * @date 2022-03-08 10:57:56
 */
@RestController
@RequestMapping("guliware/purchase")
public class PurchaseController {
    @Autowired
    private PurchaseService purchaseService;

    /**
     * 查询未领取的采购单
     */
    @RequestMapping("unreceive/list")
 //   @RequiresPermissions("guliware:purchase:list")
    public R purchaseList(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryUnreceive(params);
        return R.ok().put("page", page);
    }

    /**
     * 合并采购需求
     * */
    @PostMapping("/merge")
    public R merge(@RequestBody MergeVo mergeVo){
        purchaseService.saveMergo(mergeVo);
          return R.ok();
    }
    /**
     * 完成采购单
     * */
    @PostMapping("/done")
    public R done(DoneVo doneVo){
        purchaseService.Done(doneVo);
        return R.ok();

    }



    @RequestMapping("/list")
    //   @RequiresPermissions("guliware:purchase:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = purchaseService.queryPage(params);

        return R.ok().put("page", page);
    }
    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("guliware:purchase:info")
    public R info(@PathVariable("id") Long id){
		PurchaseEntity purchase = purchaseService.getById(id);

        return R.ok().put("purchase", purchase);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
   // @RequiresPermissions("guliware:purchase:save")
    public R save(@RequestBody PurchaseEntity purchase){
        purchase.setUpdateTime(new Date());
        purchase.setCreateTime(new Date());
		purchaseService.save(purchase);
        return R.ok();
    }



    /**
     * 领取采购:属于采购人员领取采购单的功能
     *          这个是扩展连接到员工系统的
     * **/
    @PostMapping("/received")
    public R received(@RequestBody List<Long>ids ){
        purchaseService.getReceived(ids);
    return R.ok();
    }




    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("guliware:purchase:update")
    public R update(@RequestBody PurchaseEntity purchase){
		purchase.setUpdateTime(new Date());
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
   // @RequiresPermissions("guliware:purchase:delete")
    public R delete(@RequestBody Long[] ids){
		purchaseService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
