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

import com.yuan.guli.guliorder.entity.OrderEntity;
import com.yuan.guli.guliorder.service.OrderService;
import com.yuan.common.utils.PageUtils;
import com.yuan.common.utils.R;

import javax.websocket.server.PathParam;


/**
 * 订单
 *
 * @author lzy
 * @email 1716827691@qq.com
 * @date 2022-03-08 10:53:42
 */
@RestController
@RequestMapping("guliorder/order")
public class OrderController {
    @Autowired
    private OrderService orderService;



    @RequestMapping("/getOrder/{orderSn}")
    public R getOrderBySn(@PathVariable("orderSn")String orderSn){
        OrderEntity orderEntity = orderService.getOrderBySn(orderSn);
       return R.ok().setData(orderEntity);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
 //   @RequiresPermissions("guliorder:order:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = orderService.queryPage(params);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("guliorder:order:info")
    public R info(@PathVariable("id") Long id){
		OrderEntity order = orderService.getById(id);

        return R.ok().put("order", order);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
   // @RequiresPermissions("guliorder:order:save")
    public R save(@RequestBody OrderEntity order){
		orderService.save(order);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("guliorder:order:update")
    public R update(@RequestBody OrderEntity order){
		orderService.updateById(order);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
   // @RequiresPermissions("guliorder:order:delete")
    public R delete(@RequestBody Long[] ids){
		orderService.removeByIds(Arrays.asList(ids));
        return R.ok();
    }

    /**
     * 分页展示订单列表
     */
    @RequestMapping("/orderList")
    //   @RequiresPermissions("guliorder:order:list")
    public R queryPageWithItem(@RequestBody Map<String, Object> params){
        PageUtils page = orderService.queryOrderList(params);
        return R.ok().put("page", page);
    }
}
