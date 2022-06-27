package com.yuan.guli.guliorder.controller;

import java.util.Arrays;
import java.util.Map;

//import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.yuan.guli.guliorder.entity.OrderItemEntity;
import com.yuan.guli.guliorder.service.OrderItemService;
import com.yuan.common.utils.PageUtils;
import com.yuan.common.utils.R;



/**
 * 订单项信息
 *
 * @author lzy
 * @email 1716827691@qq.com
 * @date 2022-03-08 10:53:42
 */
@RestController
@RequestMapping("guliorder/orderitem")
public class OrderItemController {
    @Autowired
    private OrderItemService orderItemService;

    /**
     * 列表
     */
    @RequestMapping("/list")
 //   @RequiresPermissions("guliorder:orderitem:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = orderItemService.queryPage(params);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("guliorder:orderitem:info")
    public R info(@PathVariable("id") Long id){
		OrderItemEntity orderItem = orderItemService.getById(id);
        return R.ok().put("orderItem", orderItem);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
   // @RequiresPermissions("guliorder:orderitem:save")
    public R save(@RequestBody OrderItemEntity orderItem){
		orderItemService.save(orderItem);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("guliorder:orderitem:update")
    public R update(@RequestBody OrderItemEntity orderItem){
		orderItemService.updateById(orderItem);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
   // @RequiresPermissions("guliorder:orderitem:delete")
    public R delete(@RequestBody Long[] ids){
		orderItemService.removeByIds(Arrays.asList(ids));
        return R.ok();
    }

}
