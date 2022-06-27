package com.yuan.guli.guliware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.yuan.common.constant.InfoEnum;
import com.yuan.common.exception.CodeEnume;
import com.yuan.guli.guliware.error.NoStockException;
import com.yuan.guli.guliware.to.LockStockResult;
import com.yuan.guli.guliware.to.WareSkuLockTo;
import com.yuan.guli.guliware.vo.SkuHasStockVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yuan.guli.guliware.entity.WareSkuEntity;
import com.yuan.guli.guliware.service.WareSkuService;
import com.yuan.common.utils.PageUtils;
import com.yuan.common.utils.R;

import javax.xml.crypto.Data;


/**
 * 商品库存
 *
 * @author lzy
 * @email 1716827691@qq.com
 * @date 2022-03-08 10:57:56
 */
@RestController
@RequestMapping("guliware/waresku")
public class WareSkuController {
    @Autowired
    private WareSkuService wareSkuService;



    /**
     * 被远程调用：查询skuid对应的商品是否有库存
     * **/
    @RequestMapping("/hasstock")
    public R getHasStock(@RequestBody List<Long> skuIds){ //将要查询的id批量传入
        //应该返回的值：sku—id  和 stock
       List<SkuHasStockVo> skuHasStockVos =  wareSkuService.getHasStock(skuIds);
       //其实：直接R.ok().put("data",skuHasStockVos)就行了，哎
       return R.ok().setData(skuHasStockVos); //直接返回
    }

    /**
     * 创建订单后的库存锁定操作
     * @param  to 传过来的需要锁定的数据
     * **/
    @RequestMapping("/stockLock")
    public R stockLock(@RequestBody WareSkuLockTo to){

        try{
            Boolean result = wareSkuService.stockLock(to);
            return R.ok();
        }catch (NoStockException ns){
         return R.error(CodeEnume.NO_STOCK_EXCEPTION.getCode(),CodeEnume.NO_STOCK_EXCEPTION.getMessage());
        }
    }


    /**
     * 列表
     */
    @RequestMapping("/list")
 //   @RequiresPermissions("guliware:waresku:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wareSkuService.querySkuWarePage(params);

        return R.ok().put("page", page);
    }






    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("guliware:waresku:info")
    public R info(@PathVariable("id") Long id){
		WareSkuEntity wareSku = wareSkuService.getById(id);

        return R.ok().put("wareSku", wareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
   // @RequiresPermissions("guliware:waresku:save")
    public R save(@RequestBody WareSkuEntity wareSku){
		wareSkuService.save(wareSku);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("guliware:waresku:update")
    public R update(@RequestBody WareSkuEntity wareSku){
		wareSkuService.updateById(wareSku);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
   // @RequiresPermissions("guliware:waresku:delete")
    public R delete(@RequestBody Long[] ids){
		wareSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
